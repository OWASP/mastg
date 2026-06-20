package org.owasp.mastestapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: MastgTest.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\b"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() {
        StringBuilder results = new StringBuilder();
        Intent implicitIntent = new Intent("android.intent.action.VIEW");
        PendingIntent.getActivity(this.context, 0, implicitIntent, 134217728);
        results.append("Created mutable PendingIntent with implicit intent\n");
        Intent explicitMutableIntent = new Intent(this.context, (Class<?>) MastgTest.class);
        PendingIntent.getService(this.context, 1, explicitMutableIntent, 33554432);
        results.append("Created explicitly mutable PendingIntent\n");
        Intent broadcastIntent = new Intent("com.example.CUSTOM_ACTION");
        PendingIntent.getBroadcast(this.context, 2, broadcastIntent, 0);
        results.append("Created broadcast PendingIntent with implicit intent\n");
        Intent $this$mastgTest_u24lambda_u240 = new Intent(this.context, (Class<?>) MastgTest.class);
        $this$mastgTest_u24lambda_u240.setPackage(this.context.getPackageName());
        PendingIntent.getActivity(this.context, 3, $this$mastgTest_u24lambda_u240, AccessibilityEventCompat.TYPE_VIEW_TARGETED_BY_SCROLL);
        results.append("Created secure PendingIntent with FLAG_IMMUTABLE\n");
        String string = results.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }
}

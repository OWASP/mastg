package org.owasp.mastestapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0005\u001a\u00020\u0006R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "mastgTest", "", "app_debug"}, k = 1, mv = {1, 9, 0}, xi = 48)
/* loaded from: classes4.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() {
        StringBuilder results = new StringBuilder();

        // FAIL: Mutable PendingIntent with implicit intent - vulnerable to hijacking
        Intent implicitIntent = new Intent("android.intent.action.VIEW");
        PendingIntent mutablePendingIntent = PendingIntent.getActivity(
            this.context,
            0,
            implicitIntent,
            134217728  // FLAG_UPDATE_CURRENT - Missing FLAG_IMMUTABLE
        );
        results.append("Created mutable PendingIntent with implicit intent\n");

        // FAIL: Explicit FLAG_MUTABLE used without justification
        Intent explicitMutableIntent = new Intent(this.context, MastgTest.class);
        PendingIntent explicitMutablePendingIntent = PendingIntent.getService(
            this.context,
            1,
            explicitMutableIntent,
            33554432  // FLAG_MUTABLE
        );
        results.append("Created explicitly mutable PendingIntent\n");

        // FAIL: Broadcast with implicit intent
        Intent broadcastIntent = new Intent("com.example.CUSTOM_ACTION");
        PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(
            this.context,
            2,
            broadcastIntent,
            0  // No flags - mutable by default on API < 31
        );
        results.append("Created broadcast PendingIntent with implicit intent\n");

        // PASS: Secure PendingIntent with FLAG_IMMUTABLE and explicit intent
        Intent secureIntent = new Intent(this.context, MastgTest.class);
        secureIntent.setPackage(this.context.getPackageName());
        PendingIntent securePendingIntent = PendingIntent.getActivity(
            this.context,
            3,
            secureIntent,
            67108864  // FLAG_IMMUTABLE
        );
        results.append("Created secure PendingIntent with FLAG_IMMUTABLE\n");

        return results.toString();
    }
}

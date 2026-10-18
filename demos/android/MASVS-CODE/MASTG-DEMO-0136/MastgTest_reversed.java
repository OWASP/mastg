package org.owasp.mastestapp;

import android.content.Context;
import android.content.Intent;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MastgTest.kt */
@Metadata(m110d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\b"}, m111d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "app_debug"}, m112k = 1, m113mv = {2, 0, 0}, m115xi = 48)
/* loaded from: classes4.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() {
        DemoResults r = new DemoResults("0x01");
        Intent implicitIntent = new Intent();
        implicitIntent.setAction("org.owasp.mastestapp.INTERNAL_ACTION");
        implicitIntent.putExtra("user_id", "12345");
        implicitIntent.putExtra("session_token", "abcde-fghij-12345");
        implicitIntent.addFlags(268435456);
        try {
            this.context.startActivity(implicitIntent);
            r.add(Status.FAIL, "Launched internal activity via implicit intent");
        } catch (Exception e) {
            r.add(Status.ERROR, e.toString());
        }
        return r.toJson();
    }
}

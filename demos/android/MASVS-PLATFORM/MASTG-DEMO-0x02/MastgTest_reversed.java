package org.owasp.mastestapp;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.core.os.EnvironmentCompat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\b"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() {
        DemoResults r = new DemoResults("AUTOVERIFY-01");
        try {
            SharedPreferences prefs = this.context.getSharedPreferences("mastg_demo_prefs", 0);
            boolean twoFactorEnabled = prefs.getBoolean("two_factor_enabled", true);
            String setBy = prefs.getString("two_factor_set_by", EnvironmentCompat.MEDIA_UNKNOWN);
            r.add(Status.PASS, "An App Link (https://deeplink.example.com) is registered WITHOUT android:autoVerify=\"true\". Because the link is unverified, any app can claim it, and opening it changes a sensitive security setting with no verification.");
            String str = "ON";
            if (Intrinsics.areEqual(setBy, "deeplink")) {
                Status status = Status.FAIL;
                if (!twoFactorEnabled) {
                    str = "OFF";
                }
                r.add(status, "A sensitive security setting was changed via an unverified deep link. Two-factor authentication is now " + str + ".");
            } else {
                Status status2 = Status.PASS;
                if (!twoFactorEnabled) {
                    str = "OFF";
                }
                r.add(status2, "Current two-factor authentication state: " + str + ". It has not been changed via deep link yet.");
            }
        } catch (Exception e) {
            r.add(Status.ERROR, e.toString());
        }
        return r.toJson();
    }
}

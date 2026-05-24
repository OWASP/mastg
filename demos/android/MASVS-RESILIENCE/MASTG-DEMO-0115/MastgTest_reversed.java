package org.owasp.mastestapp;

import android.content.Context;
import android.os.Debug;
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
        DemoResults r = new DemoResults("0x40");
        try {
            boolean debuggerConnected = Debug.isDebuggerConnected();
            if (debuggerConnected) {
                r.add(Status.FAIL, "Debug.isDebuggerConnected reports an attached JDWP debugger.");
            } else {
                r.add(Status.PASS, "Debug.isDebuggerConnected reports no attached JDWP debugger.");
            }
        } catch (Exception e) {
            r.add(Status.ERROR, "Unexpected error while running anti-debugging checks: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        return r.toJson();
    }
}

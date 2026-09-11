package org.owasp.mastestapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: MastgTest.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000 \u000f2\u00020\u0001:\u0001\u000fB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\n\u001a\u00020\u000bJ\b\u0010\f\u001a\u00020\rH\u0002J\u000b\u0010\u000e\u001a\u0004\u0018\u00010\u000bH\u0082 R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u00020\u0007X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\u0010"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "shouldRunInMainThread", "", "getShouldRunInMainThread", "()Z", "mastgTest", "", "closeApp", "", "findRootArtifactPath", "Companion", "app_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class MastgTest {
    private final Context context;
    private final boolean shouldRunInMainThread;
    public static final int $stable = 8;

    private final native String findRootArtifactPath();

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.shouldRunInMainThread = true;
    }

    public final boolean getShouldRunInMainThread() {
        return this.shouldRunInMainThread;
    }

    public final String mastgTest() {
        DemoResults demoResults = new DemoResults("0x52");
        try {
            String strFindRootArtifactPath = findRootArtifactPath();
            if (strFindRootArtifactPath != null) {
                String str = "Detected root artifact path '" + strFindRootArtifactPath + "'. The app closes when a monitored su path is found.";
                Log.w("MASTG-DEMO-0133", str);
                demoResults.add(Status.FAIL, str);
                closeApp();
            } else {
                demoResults.add(Status.PASS, "No monitored su path was found.");
            }
            return demoResults.toJson();
        } catch (Exception e) {
            demoResults.add(Status.ERROR, e.toString());
            return demoResults.toJson();
        }
    }

    private final void closeApp() {
        Context context = this.context;
        Activity activity = context instanceof Activity ? (Activity) context : null;
        if (activity == null) {
            return;
        }
        activity.finishAffinity();
    }

    static {
        System.loadLibrary("rootcheck");
    }
}

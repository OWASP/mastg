package org.owasp.mastestapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import kotlin.Metadata;
import kotlin.io.ByteStreamsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u0000 \u00132\u00020\u0001:\u0001\u0013B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\n\u001a\u00020\u000bJ\u0006\u0010\f\u001a\u00020\rJ\u0016\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u00020\u0007X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\u0014"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "shouldRunInMainThread", "", "getShouldRunInMainThread", "()Z", "writeSensitiveData", "", "mastgTest", "", "handleResult", "activity", "Landroid/app/Activity;", "uri", "Landroid/net/Uri;", "Companion", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int REQUEST_FILE = 1001;
    private final Context context;
    private final boolean shouldRunInMainThread;
    public static final int $stable = 8;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.shouldRunInMainThread = true;
    }

    public final boolean getShouldRunInMainThread() {
        return this.shouldRunInMainThread;
    }

    public final void writeSensitiveData() {
        SharedPreferences prefs = this.context.getSharedPreferences("session", 0);
        prefs.edit().putString("auth_token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoiYWRtaW4ifQ.secret").putString("refresh_token", "rt_8f14e45f-ceea-367f-a27f-abc123def456").putString("user_email", "admin@example.com").putString("api_key", "sk-live-1234567890abcdef").putString("session_id", "sess_a1b2c3d4e5f6").putString("credit_card_last4", "4242").apply();
        Log.d("MASTG-DEMO", "Dummy sensitive data written to SharedPreferences");
    }

    public final String mastgTest() {
        DemoResults r = new DemoResults("0060");
        Intent intent = new Intent();
        intent.setAction("org.owasp.mastestapp.REQUEST_FILE");
        try {
            Context context = this.context;
            Intrinsics.checkNotNull(context, "null cannot be cast to non-null type android.app.Activity");
            ((Activity) context).startActivityForResult(intent, 1001);
            r.add(Status.FAIL, "Implicit intent launched with action REQUEST_FILE — any app can intercept");
        } catch (Exception e) {
            r.add(Status.ERROR, e.toString());
        }
        return r.toJson();
    }

    public final String handleResult(Activity activity, Uri uri) throws IOException {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(uri, "uri");
        DemoResults r = new DemoResults("0060");
        try {
            File file = new File(activity.getExternalCacheDir(), "tmp");
            file.createNewFile();
            InputStream inputStreamOpenInputStream = activity.getContentResolver().openInputStream(uri);
            if (inputStreamOpenInputStream != null) {
                FileOutputStream fileOutputStream = inputStreamOpenInputStream;
                try {
                    InputStream input = fileOutputStream;
                    fileOutputStream = new FileOutputStream(file);
                    try {
                        FileOutputStream output = fileOutputStream;
                        long jCopyTo$default = ByteStreamsKt.copyTo$default(input, output, 0, 2, null);
                        CloseableKt.closeFinally(fileOutputStream, null);
                        Long.valueOf(jCopyTo$default);
                        CloseableKt.closeFinally(fileOutputStream, null);
                    } finally {
                    }
                } finally {
                }
            }
            r.add(Status.FAIL, "File copied to world-readable location: " + file.getAbsolutePath());
            Log.d("MASTG-DEMO", "Copied URI content to: " + file.getAbsolutePath());
            String content = FilesKt.readText$default(file, null, 1, null);
            Log.d("MASTG-DEMO", "Stolen file content:\n" + content);
        } catch (Exception e) {
            r.add(Status.ERROR, "Failed to copy: " + e.getMessage());
        }
        return r.toJson();
    }
}

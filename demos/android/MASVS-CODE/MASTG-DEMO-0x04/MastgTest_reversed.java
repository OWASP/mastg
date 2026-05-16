package org.owasp.mastestapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.io.ByteStreamsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\n\u001a\u00020\u000bJ\u0006\u0010\f\u001a\u00020\rJ\u0016\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012J\u001a\u0010\u0013\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u00020\u0007X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\u0015"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "shouldRunInMainThread", "", "getShouldRunInMainThread", "()Z", "writeSensitiveData", "", "mastgTest", "", "handleResult", "activity", "Landroid/app/Activity;", "uri", "Landroid/net/Uri;", "getFileNameFromUri", "Companion", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int REQUEST_LIBRARY = 1002;
    private final Context context;
    private final boolean shouldRunInMainThread;
    public static final int $stable = 8;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.shouldRunInMainThread = true;
    }

    public final boolean getShouldRunInMainThread() {                                                                                                             return this.shouldRunInMainThread;
    }

    public final void writeSensitiveData() {
        SharedPreferences prefs = this.context.getSharedPreferences("session", 0);
        prefs.edit().putString("auth_token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoiYWRtaW4ifQ.secret").putString("api_key", "sk-live-1234567890abcdef").apply();
        File configFile = new File(this.context.getFilesDir(), "lib_config.json");
        FilesKt.writeText$default(configFile, "{\"library\": \"legit_plugin\", \"version\": \"1.0\", \"trusted\": true}", null, 2, null);
        Log.d("MASTG-DEMO", "Dummy sensitive data and lib config written");                                                                                   }

    public final String mastgTest() {
        DemoResults r = new DemoResults("0061");
        Intent intent = new Intent();
        intent.setAction("org.owasp.mastestapp.REQUEST_LIBRARY");
        try {
            Context context = this.context;
            Intrinsics.checkNotNull(context, "null cannot be cast to non-null type android.app.Activity");
            ((Activity) context).startActivityForResult(intent, 1002);
            r.add(Status.FAIL, "Implicit intent launched with action REQUEST_LIBRARY — any app can intercept");
        } catch (Exception e) {
            r.add(Status.ERROR, e.toString());
        }
        return r.toJson();
    }

    public final String handleResult(Activity activity, Uri uri) throws FileNotFoundException {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(uri, "uri");
        DemoResults r = new DemoResults("0061");
        try {
            String fileNameFromUri = getFileNameFromUri(activity, uri);
            if (fileNameFromUri == null) {
                fileNameFromUri = "downloaded_lib.so";
            }
            String fileName = fileNameFromUri;
            Log.d("MASTG-DEMO", "Content provider returned filename: " + fileName);
            File targetFile = new File(activity.getFilesDir(), fileName);
            File parentFile = targetFile.getParentFile();
            if (parentFile != null) {
                parentFile.mkdirs();
            }
            InputStream inputStreamOpenInputStream = activity.getContentResolver().openInputStream(uri);
            if (inputStreamOpenInputStream != null) {
                FileOutputStream fileOutputStream = inputStreamOpenInputStream;
                try {
                    InputStream input = fileOutputStream;
                    fileOutputStream = new FileOutputStream(targetFile);
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
            Log.d("MASTG-DEMO", "File written to: " + targetFile.getAbsolutePath());
            String content = FilesKt.readText$default(targetFile, null, 1, null);
            Log.d("MASTG-DEMO", "File content:\n" + content);
            r.add(Status.FAIL, "Attacker-controlled file written to: " + targetFile.getAbsolutePath() + "\nContent: " + content);
        } catch (Exception e) {
            r.add(Status.ERROR, "Failed: " + e.getMessage());
            Log.e("MASTG-DEMO", "Error: " + e.getMessage());
        }
        return r.toJson();
    }

    private final String getFileNameFromUri(Activity activity, Uri uri) throws IOException {
        int nameIndex;
        String string = null;
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            Cursor cursor2 = cursor;
            try {
                Cursor it = cursor2;
                if (it.moveToFirst() && (nameIndex = it.getColumnIndex("_display_name")) >= 0) {
                    string = it.getString(nameIndex);
                }
                Unit unit = Unit.INSTANCE;
                CloseableKt.closeFinally(cursor2, null);
            } finally {
            }
        }
        return string;
    }
}
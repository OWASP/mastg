package org.owasp.mastestapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.io.CloseableKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J\b\u0010\b\u001a\u00020\tH\u0002J\b\u0010\n\u001a\u00020\tH\u0002J\b\u0010\u000b\u001a\u00020\tH\u0002J\b\u0010\f\u001a\u00020\tH\u0002J\u0012\u0010\r\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u000e\u001a\u00020\u0007H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "checkForSuBinary", "", "checkForRootPackages", "checkForTestKeys", "checkForDangerousProps", "getSystemProperty", "key", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() {
        List checks = new ArrayList();
        if (checkForSuBinary()) {
            checks.add("✓ Found su binary");
        } else {
            checks.add("✗ No su binary found");
        }
        if (checkForRootPackages()) {
            checks.add("✓ Found root management apps");
        } else {
            checks.add("✗ No root management apps found");
        }
        if (checkForTestKeys()) {
            checks.add("✓ Device has test-keys build");
        } else {
            checks.add("✗ Device has release-keys build");
        }
        if (checkForDangerousProps()) {
            checks.add("✓ Found dangerous system properties");
        } else {
            checks.add("✗ No dangerous system properties");
        }
        boolean isRooted = checkForSuBinary() || checkForRootPackages() || checkForTestKeys() || checkForDangerousProps();
        return "Root Detection Results:\n\n" + CollectionsKt.joinToString$default(checks, "\n", null, null, 0, null, null, 62, null) + "\n\nDevice appears to be rooted: " + isRooted;
    }

    private final boolean checkForSuBinary() {
        String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) {
                return true;
            }
        }
        return false;
    }

    private final boolean checkForRootPackages() throws PackageManager.NameNotFoundException {
        String[] packages = {"com.noshufou.android.su", "com.noshufou.android.su.elite", "eu.chainfire.supersu", "com.koushikdutta.superuser", "com.thirdparty.superuser", "com.yellowes.su", "com.topjohnwu.magisk", "com.kingroot.kinguser", "com.kingo.root", "com.smedialink.oneclickroot", "com.zhiqupk.root.global", "com.alephzain.framaroot"};
        for (String packageName : packages) {
            try {
                this.context.getPackageManager().getPackageInfo(packageName, 0);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
        return false;
    }

    private final boolean checkForTestKeys() {
        String buildTags = Build.TAGS;
        return buildTags != null && StringsKt.contains$default((CharSequence) buildTags, (CharSequence) "test-keys", false, 2, (Object) null);
    }

    private final boolean checkForDangerousProps() throws IOException {
        Map dangerousProps = MapsKt.mapOf(TuplesKt.to("ro.debuggable", "1"), TuplesKt.to("ro.secure", "0"));
        for (Map.Entry entry : dangerousProps.entrySet()) {
            String prop = (String) entry.getKey();
            String value = (String) entry.getValue();
            String propValue = getSystemProperty(prop);
            if (Intrinsics.areEqual(propValue, value)) {
                return true;
            }
        }
        return false;
    }

    private final String getSystemProperty(String key) throws IOException {
        try {
            Process process = Runtime.getRuntime().exec("getprop " + key);
            InputStream inputStream = process.getInputStream();
            Intrinsics.checkNotNullExpressionValue(inputStream, "getInputStream(...)");
            Reader inputStreamReader = new InputStreamReader(inputStream, Charsets.UTF_8);
            BufferedReader bufferedReader = inputStreamReader instanceof BufferedReader ? (BufferedReader) inputStreamReader : new BufferedReader(inputStreamReader, 8192);
            try {
                BufferedReader it = bufferedReader;
                String string = StringsKt.trim((CharSequence) TextStreamsKt.readText(it)).toString();
                CloseableKt.closeFinally(bufferedReader, null);
                return string;
            } finally {
            }
        } catch (Exception e) {
            return null;
        }
    }
}

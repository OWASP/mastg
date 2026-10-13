package org.owasp.mastestapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.ArrayIteratorKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0007H\u0002J\u000e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00070\fH\u0002J\u000e\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00070\fH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "promptUserForLiability", "", "message", "checkForeignDexesInMaps", "", "checkInstrumentationFramesInStacks", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() {
        DemoResults r = new DemoResults("0x4A");
        boolean anyFail = false;
        try {
            List foreignDexes = checkForeignDexesInMaps();
            if (!foreignDexes.isEmpty()) {
                r.add(Status.FAIL, "Foreign DEX/APK mapped into process: " + CollectionsKt.joinToString$default(foreignDexes, ", ", null, null, 0, null, null, 62, null));
                anyFail = true;
            } else {
                r.add(Status.PASS, "No foreign DEX/APK mapped into process.");
            }
        } catch (Exception e) {
            r.add(Status.ERROR, "/proc/self/maps inspection failed: " + e);
        }
        try {
            List frames = checkInstrumentationFramesInStacks();
            if (!frames.isEmpty()) {
                r.add(Status.FAIL, "Instrumentation frames on stack: " + CollectionsKt.joinToString$default(frames, ", ", null, null, 0, null, null, 62, null));
                anyFail = true;
            } else {
                r.add(Status.PASS, "No Xposed/LSPosed/Frida frames found in any thread's stack.");
            }
        } catch (Exception e2) {
            r.add(Status.ERROR, "Stack-trace inspection failed: " + e2);
        }
        if (anyFail) {
            promptUserForLiability("Reverse-engineering or instrumentation tooling (Xposed/LSPosed) was detected on this device. Continued use may compromise app security and data integrity. Tap \"Accept Liability\" to acknowledge the risk and continue, or \"Exit\" to close the app.");
        }
        return r.toJson();
    }

    private final void promptUserForLiability(final String message) {
        Context context = this.context;
        final Activity activity = context instanceof Activity ? (Activity) context : null;
        if (activity == null) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                MastgTest.promptUserForLiability$lambda$2(activity, message);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void promptUserForLiability$lambda$2(final Activity activity, String message) {
        Intrinsics.checkNotNullParameter(activity, "$activity");
        Intrinsics.checkNotNullParameter(message, "$message");
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        new AlertDialog.Builder(activity).setTitle("Security Warning").setMessage(message).setCancelable(false).setPositiveButton("Accept Liability", new DialogInterface.OnClickListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                MastgTest.promptUserForLiability$lambda$2$lambda$1(activity, dialogInterface, i);
            }
        }).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void promptUserForLiability$lambda$2$lambda$1(Activity activity, DialogInterface dialogInterface, int i) {
        Intrinsics.checkNotNullParameter(activity, "$activity");
        activity.finishAffinity();
    }

    private final List<String> checkForeignDexesInMaps() throws IOException {
        final String ownPkg = this.context.getPackageName();
        final LinkedHashSet hits = new LinkedHashSet();
        BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/self/maps"));
        try {
            BufferedReader br = bufferedReader;
            TextStreamsKt.forEachLine(br, new Function1() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda2
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return MastgTest.checkForeignDexesInMaps$lambda$4$lambda$3(ownPkg, hits, (String) obj);
                }
            });
            Unit unit = Unit.INSTANCE;
            CloseableKt.closeFinally(bufferedReader, null);
            return CollectionsKt.toList(hits);
        } finally {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit checkForeignDexesInMaps$lambda$4$lambda$3(String $ownPkg, LinkedHashSet hits, String line) {
        Intrinsics.checkNotNullParameter(hits, "$hits");
        Intrinsics.checkNotNullParameter(line, "line");
        int idx = StringsKt.indexOf$default((CharSequence) line, "/data/app/", 0, false, 6, (Object) null);
        if (idx < 0) {
            return Unit.INSTANCE;
        }
        String strSubstring = line.substring(idx);
        Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
        String path = StringsKt.substringBefore$default(strSubstring, ' ', (String) null, 2, (Object) null);
        if (!StringsKt.endsWith$default(path, ".apk", false, 2, (Object) null)) {
            return Unit.INSTANCE;
        }
        if (StringsKt.contains$default((CharSequence) path, (CharSequence) ("/" + $ownPkg + "-"), false, 2, (Object) null) || StringsKt.contains$default((CharSequence) path, (CharSequence) ("/" + $ownPkg + "/"), false, 2, (Object) null)) {
            return Unit.INSTANCE;
        }
        String pkg = StringsKt.substringBefore$default(StringsKt.substringAfter$default(StringsKt.substringAfter$default(path, "/data/app/", (String) null, 2, (Object) null), '/', (String) null, 2, (Object) null), '-', (String) null, 2, (Object) null);
        hits.add(pkg);
        return Unit.INSTANCE;
    }

    private final List<String> checkInstrumentationFramesInStacks() {
        List needles = CollectionsKt.listOf((Object[]) new String[]{"de.robv.android.xposed", "org.lsposed.lspd", "org.lsposed.", "lsphooker_", "lsplant", "edxposed", "re.frida"});
        LinkedHashSet hits = new LinkedHashSet();
        try {
            this.context.getPackageManager().getPackageInfo("___xposed_probe_" + System.nanoTime(), 0);
        } catch (Throwable e) {
            checkInstrumentationFramesInStacks$scan(needles, hits, "getPackageInfo", e.getStackTrace());
        }
        try {
            Runtime.getRuntime().exec(new String[]{"/__xposed_probe_" + System.nanoTime()});
        } catch (Throwable e2) {
            checkInstrumentationFramesInStacks$scan(needles, hits, "Runtime.exec", e2.getStackTrace());
        }
        try {
            new File("/__xposed_probe_" + System.nanoTime()).exists();
            Throwable t = new Throwable("post-File.exists probe");
            checkInstrumentationFramesInStacks$scan(needles, hits, "File.exists.probe", t.getStackTrace());
        } catch (Throwable e3) {
            checkInstrumentationFramesInStacks$scan(needles, hits, "File.exists", e3.getStackTrace());
        }
        try {
            Map all = Thread.getAllStackTraces();
            Intrinsics.checkNotNull(all);
            for (Map.Entry<Thread, StackTraceElement[]> entry : all.entrySet()) {
                Thread thread = entry.getKey();
                StackTraceElement[] frames = entry.getValue();
                checkInstrumentationFramesInStacks$scan(needles, hits, "thread=" + thread.getName(), frames);
            }
        } catch (Throwable th) {
        }
        return CollectionsKt.toList(hits);
    }

    private static final void checkInstrumentationFramesInStacks$scan(List<String> list, LinkedHashSet<String> linkedHashSet, String label, StackTraceElement[] frames) {
        if (frames == null) {
            return;
        }
        Iterator it = ArrayIteratorKt.iterator(frames);
        while (it.hasNext()) {
            StackTraceElement f = (StackTraceElement) it.next();
            String className = f.getClassName();
            Intrinsics.checkNotNullExpressionValue(className, "getClassName(...)");
            String low = className.toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue(low, "toLowerCase(...)");
            for (String n : list) {
                String lowerCase = n.toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
                if (StringsKt.contains$default((CharSequence) low, (CharSequence) lowerCase, false, 2, (Object) null)) {
                    linkedHashSet.add(label + ": " + f.getClassName() + "." + f.getMethodName());
                }
            }
        }
    }
}

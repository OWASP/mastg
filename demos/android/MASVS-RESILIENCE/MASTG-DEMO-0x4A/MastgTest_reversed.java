package org.owasp.mastestapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.ArrayIteratorKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0007H\u0002J\u000e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00070\fH\u0002J\u000e\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00070\fH\u0002J\u000e\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00070\fH\u0002J\u000e\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00070\fH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "promptUserForLiability", "", "message", "checkKnownXposedManagerPackages", "", "checkHookedMethodSignatures", "checkForeignDexesInMaps", "checkInstrumentationFramesInStacks", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
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
            List managers = checkKnownXposedManagerPackages();
            if (!managers.isEmpty()) {
                r.add(Status.FAIL, "Xposed Manager package(s) installed: " + CollectionsKt.joinToString$default(managers, ", ", null, null, 0, null, null, 62, null));
                anyFail = true;
            } else {
                r.add(Status.PASS, "No known Xposed Manager package found via PackageManager.");
            }
        } catch (Exception e) {
            r.add(Status.ERROR, "Manager lookup failed: " + e);
        }
        try {
            List tampered = checkHookedMethodSignatures();
            if (!tampered.isEmpty()) {
                r.add(Status.FAIL, "Methods appear to be hooked: " + CollectionsKt.joinToString$default(tampered, ", ", null, null, 0, null, null, 62, null));
                anyFail = true;
            } else {
                r.add(Status.PASS, "Audited native methods retain their NATIVE modifier.");
            }
        } catch (Exception e2) {
            r.add(Status.ERROR, "Method-descriptor check failed: " + e2);
        }
        try {
            List foreignDexes = checkForeignDexesInMaps();
            if (!foreignDexes.isEmpty()) {
                r.add(Status.FAIL, "Foreign DEX/APK mapped into process: " + CollectionsKt.joinToString$default(foreignDexes, ", ", null, null, 0, null, null, 62, null));
                anyFail = true;
            } else {
                r.add(Status.PASS, "No foreign DEX/APK mapped into process.");
            }
        } catch (Exception e3) {
            r.add(Status.ERROR, "/proc/self/maps inspection failed: " + e3);
        }
        try {
            List frames = checkInstrumentationFramesInStacks();
            if (!frames.isEmpty()) {
                r.add(Status.FAIL, "Instrumentation frames on stack: " + CollectionsKt.joinToString$default(frames, ", ", null, null, 0, null, null, 62, null));
                anyFail = true;
            } else {
                r.add(Status.PASS, "No Xposed/LSPosed/Frida frames found in any thread's stack.");
            }
        } catch (Exception e4) {
            r.add(Status.ERROR, "Stack-trace inspection failed: " + e4);
        }
        if (anyFail) {
            promptUserForLiability("Reverse-engineering or instrumentation tooling (Xposed/LSPosed/Frida) was detected on this device. Continued use may compromise app security and data integrity. Tap \"Accept Liability\" to acknowledge the risk and continue, or \"Exit\" to close the app.");
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

    private final List<String> checkKnownXposedManagerPackages() throws PackageManager.NameNotFoundException {
        boolean z;
        Iterable knownManagers = CollectionsKt.listOf((Object[]) new String[]{"de.robv.android.xposed.installer", "org.meowcat.edxposed.manager", "io.va.exposed", "com.solohsu.android.edxp.manager", "org.lsposed.manager"});
        PackageManager pm = this.context.getPackageManager();
        Iterable $this$filter$iv = knownManagers;
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv : $this$filter$iv) {
            String pkg = (String) element$iv$iv;
            try {
                pm.getPackageInfo(pkg, 0);
                z = true;
            } catch (PackageManager.NameNotFoundException e) {
                z = false;
            }
            if (z) {
                destination$iv$iv.add(element$iv$iv);
            }
        }
        return (List) destination$iv$iv;
    }

    private final List<String> checkHookedMethodSignatures() throws NoSuchMethodException, ClassNotFoundException, SecurityException {
        List tampered = new ArrayList();
        Map targets = MapsKt.mapOf(TuplesKt.to("java.lang.System", CollectionsKt.listOf((Object[]) new String[]{"currentTimeMillis", "nanoTime"})), TuplesKt.to("java.lang.Object", CollectionsKt.listOf((Object[]) new String[]{"notify", "notifyAll"})), TuplesKt.to("java.lang.Thread", CollectionsKt.listOf("currentThread")));
        for (Map.Entry entry : targets.entrySet()) {
            String cls = (String) entry.getKey();
            List<String> methods = (List) entry.getValue();
            try {
                Class c = Class.forName(cls);
                for (String mName : methods) {
                    Method m = c.getDeclaredMethod(mName, new Class[0]);
                    if (!Modifier.isNative(m.getModifiers())) {
                        tampered.add(cls + "." + mName + " (native bit cleared, modifiers=0x" + Integer.toHexString(m.getModifiers()) + ")");
                    }
                }
            } catch (Exception e) {
            }
        }
        return tampered;
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
                    return MastgTest.checkForeignDexesInMaps$lambda$5$lambda$4(ownPkg, hits, (String) obj);
                }
            });
            Unit unit = Unit.INSTANCE;
            CloseableKt.closeFinally(bufferedReader, null);
            return CollectionsKt.toList(hits);
        } finally {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit checkForeignDexesInMaps$lambda$5$lambda$4(String $ownPkg, LinkedHashSet hits, String line) {
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

    /* JADX WARN: Removed duplicated region for block: B:34:0x015a A[Catch: all -> 0x01d5, TRY_LEAVE, TryCatch #3 {all -> 0x01d5, blocks: (B:25:0x0140, B:32:0x0156, B:34:0x015a, B:38:0x017a, B:39:0x0189, B:41:0x018f), top: B:66:0x0140 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private final List<String> checkInstrumentationFramesInStacks() {
        int length;
        int i;
        List nativeThreadNeedles;
        int i2 = 1;
        List needles = CollectionsKt.listOf((Object[]) new String[]{"de.robv.android.xposed", "org.lsposed.lspd", "org.lsposed.", "lsphooker_", "lsplant", "edxposed", "re.frida", "gum-js"});
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
        List nativeThreadNeedles2 = CollectionsKt.listOf((Object[]) new String[]{"gum-js", "gmain", "pool-frida", "frida", "linjector", "lspd", "xposed", "lsphooker"});
        try {
            File[] fileArrListFiles = new File("/proc/self/task").listFiles();
            if (fileArrListFiles == null) {
                try {
                    fileArrListFiles = new File[0];
                    File[] tasks = fileArrListFiles;
                    length = tasks.length;
                    i = 0;
                    while (i < length) {
                        File task = tasks[i];
                        File commFile = new File(task, "comm");
                        if (commFile.canRead()) {
                            try {
                                String name = StringsKt.trim((CharSequence) FilesKt.readText$default(commFile, null, i2, null)).toString();
                                String low = name.toLowerCase(Locale.ROOT);
                                Intrinsics.checkNotNullExpressionValue(low, "toLowerCase(...)");
                                Iterator it = nativeThreadNeedles2.iterator();
                                while (true) {
                                    if (!it.hasNext()) {
                                        nativeThreadNeedles = nativeThreadNeedles2;
                                        break;
                                    }
                                    String n = (String) it.next();
                                    nativeThreadNeedles = nativeThreadNeedles2;
                                    try {
                                        if (StringsKt.contains$default((CharSequence) low, (CharSequence) n, false, 2, (Object) null)) {
                                            hits.add("native-thread: " + name);
                                            break;
                                        }
                                        nativeThreadNeedles2 = nativeThreadNeedles;
                                    } catch (Throwable th2) {
                                    }
                                }
                            } catch (Throwable th3) {
                                nativeThreadNeedles = nativeThreadNeedles2;
                            }
                        } else {
                            nativeThreadNeedles = nativeThreadNeedles2;
                        }
                        i++;
                        nativeThreadNeedles2 = nativeThreadNeedles;
                        i2 = 1;
                    }
                } catch (Throwable th4) {
                }
            } else {
                File[] tasks2 = fileArrListFiles;
                length = tasks2.length;
                i = 0;
                while (i < length) {
                }
            }
        } catch (Throwable th5) {
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

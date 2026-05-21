package org.owasp.mastestapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import androidx.compose.runtime.ComposerKt;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0007H\u0002J\b\u0010\u000b\u001a\u00020\fH\u0002J\u000e\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00070\u000eH\u0002J\u000e\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00070\u000eH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "promptUserForLiability", "", "message", "checkFridaDefaultPort", "", "checkFridaProcesses", "", "checkFridaLibraries", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:9:0x0023 -> B:30:0x003c). Please report as a decompilation issue!!! */
    public final String mastgTest() {
        DemoResults r = new DemoResults("0x48");
        boolean anyFail = false;
        try {
            boolean portFound = checkFridaDefaultPort();
            if (portFound) {
                r.add(Status.FAIL, "Frida default port (27042) is open — instrumentation detected.");
                anyFail = true;
            } else {
                r.add(Status.PASS, "Frida default port (27042) is closed.");
            }
        } catch (Exception e) {
            r.add(Status.ERROR, "Port check failed: " + e);
        }
        try {
            List matches = checkFridaProcesses();
            if (!matches.isEmpty()) {
                r.add(Status.FAIL, "Suspicious processes found: " + CollectionsKt.joinToString$default(matches, ", ", null, null, 0, null, null, 62, null));
                anyFail = true;
            } else {
                r.add(Status.PASS, "No Frida-related process names found under /proc.");
            }
        } catch (Exception e2) {
            r.add(Status.ERROR, "Process enumeration failed: " + e2);
        }
        try {
            List libsFound = checkFridaLibraries();
            if (!libsFound.isEmpty()) {
                r.add(Status.FAIL, "Injected libraries detected in /proc/self/maps: " + CollectionsKt.joinToString$default(libsFound, ", ", null, null, 0, null, null, 62, null));
                anyFail = true;
            } else {
                r.add(Status.PASS, "No Frida libraries mapped into the process.");
            }
        } catch (Exception e3) {
            r.add(Status.ERROR, "Maps check failed: " + e3);
        }
        if (anyFail) {
            promptUserForLiability("Reverse-engineering or instrumentation tooling (Frida) was detected on this device. Continued use may compromise app security and data integrity. Tap \"Accept Liability\" to acknowledge the risk and continue, or \"Exit\" to close the app.");
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
        new AlertDialog.Builder(activity).setTitle("Security Warning").setMessage(message).setCancelable(false).setPositiveButton("Accept Liability", new DialogInterface.OnClickListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda2
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

    private final boolean checkFridaDefaultPort() throws IOException {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress("127.0.0.1", 27042), ComposerKt.invocationKey);
            try {
                socket.close();
            } catch (Exception e) {
            }
            return true;
        } catch (Exception e2) {
            try {
                socket.close();
            } catch (Exception e3) {
            }
            return false;
        } catch (Throwable th) {
            try {
                socket.close();
            } catch (Exception e4) {
            }
            throw th;
        }
    }

    private final List<String> checkFridaProcesses() {
        int i = 1;
        List needles = CollectionsKt.listOf((Object[]) new String[]{"frida-server", "frida-helper", "frida-agent", "gum-js-loop", "gmain"});
        List matches = new ArrayList();
        File proc = new File("/proc");
        File[] pidDirs = proc.listFiles(new FileFilter() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda0
            @Override // java.io.FileFilter
            public final boolean accept(File file) {
                return MastgTest.checkFridaProcesses$lambda$4(file);
            }
        });
        if (pidDirs == null) {
            return matches;
        }
        int length = pidDirs.length;
        int i2 = 0;
        while (i2 < length) {
            File pidDir = pidDirs[i2];
            File cmdline = new File(pidDir, "cmdline");
            if (cmdline.canRead()) {
                try {
                    String name = StringsKt.trim((CharSequence) StringsKt.replace$default(FilesKt.readText$default(cmdline, null, i, null), (char) 0, ' ', false, 4, (Object) null)).toString();
                    Iterator it = needles.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            String needle = (String) it.next();
                            if (StringsKt.contains$default((CharSequence) name, (CharSequence) needle, false, 2, (Object) null)) {
                                matches.add(pidDir.getName() + ":" + name);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
            i2++;
            i = 1;
        }
        return matches;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean checkFridaProcesses$lambda$4(File f) {
        CharSequence $this$all$iv;
        if (!f.isDirectory()) {
            return false;
        }
        CharSequence name = f.getName();
        Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
        CharSequence $this$all$iv2 = name;
        int i = 0;
        while (true) {
            if (i < $this$all$iv2.length()) {
                char element$iv = $this$all$iv2.charAt(i);
                if (!Character.isDigit(element$iv)) {
                    $this$all$iv = null;
                    break;
                }
                i++;
            } else {
                $this$all$iv = 1;
                break;
            }
        }
        return $this$all$iv != null;
    }

    private final List<String> checkFridaLibraries() throws IOException {
        final List needles = CollectionsKt.listOf((Object[]) new String[]{"frida-agent", "libfrida", "frida-gadget", "gum-js-loop", "linjector", "/gum"});
        final Set hits = new LinkedHashSet();
        BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/self/maps"));
        try {
            BufferedReader br = bufferedReader;
            TextStreamsKt.forEachLine(br, new Function1() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda4
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return MastgTest.checkFridaLibraries$lambda$6$lambda$5(needles, hits, (String) obj);
                }
            });
            Unit unit = Unit.INSTANCE;
            CloseableKt.closeFinally(bufferedReader, null);
            return CollectionsKt.toList(hits);
        } finally {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit checkFridaLibraries$lambda$6$lambda$5(List needles, Set hits, String line) {
        Intrinsics.checkNotNullParameter(needles, "$needles");
        Intrinsics.checkNotNullParameter(hits, "$hits");
        Intrinsics.checkNotNullParameter(line, "line");
        Iterator it = needles.iterator();
        while (it.hasNext()) {
            String needle = (String) it.next();
            if (StringsKt.contains((CharSequence) line, (CharSequence) needle, true)) {
                hits.add(needle);
            }
        }
        return Unit.INSTANCE;
    }
}

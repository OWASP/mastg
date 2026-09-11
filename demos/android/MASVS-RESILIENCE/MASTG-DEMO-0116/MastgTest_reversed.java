package org.owasp.mastestapp;

import android.content.Context;
import android.os.Build;
import android.system.OsConstants;
import java.io.File;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.text.StringsKt;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\b\u0007\u0018\u0000 \u000f2\u00020\u0001:\u0001\u000fB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J\t\u0010\b\u001a\u00020\tH\u0082 J\t\u0010\n\u001a\u00020\tH\u0082 J\t\u0010\u000b\u001a\u00020\tH\u0082 J\b\u0010\f\u001a\u00020\tH\u0002J\u0010\u0010\r\u001a\u00020\u00072\u0006\u0010\u000e\u001a\u00020\tH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "getTracerPidNative", "", "getTracerPidInlineSyscallNative", "ptraceSelfDetectNative", "getTracerPidFromProcStatus", "errnoName", "errno", "Companion", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    private static String nativeLibraryLoadError;
    private static final boolean nativeLibraryLoaded;
    private final Context context;
    public static final int $stable = 8;

    private final native int getTracerPidInlineSyscallNative();

    private final native int getTracerPidNative();

    private final native int ptraceSelfDetectNative();

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    static {
        boolean z;
        try {
            System.loadLibrary("tracerpidcheck");
            z = true;
        } catch (Throwable t) {
            String message = t.getMessage();
            if (message == null) {
                message = t.toString();
            }
            nativeLibraryLoadError = message;
            z = false;
        }
        nativeLibraryLoaded = z;
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:30:0x011f -> B:55:0x013c). Please report as a decompilation issue!!! */
    public final String mastgTest() {
        DemoResults r = new DemoResults("0x41");
        try {
            int tracerPidKotlin = getTracerPidFromProcStatus();
            if (tracerPidKotlin > 0) {
                r.add(Status.FAIL, "Kotlin check: native debugger detected via TracerPid=" + tracerPidKotlin + " in /proc/self/status.");
            } else {
                r.add(Status.PASS, "Kotlin check: no native debugger detected via TracerPid=" + tracerPidKotlin + " in /proc/self/status.");
            }
        } catch (IllegalStateException e) {
            Status status = Status.ERROR;
            String message = e.getMessage();
            if (message == null) {
                message = e.toString();
            }
            r.add(status, "Kotlin check failed: " + message);
        } catch (NumberFormatException e2) {
            r.add(Status.ERROR, "Kotlin check failed: could not parse TracerPid in /proc/self/status: " + e2.getMessage());
        } catch (Exception e3) {
            r.add(Status.ERROR, "Kotlin check failed: unexpected error while checking TracerPid: " + e3.getMessage());
        }
        if (!nativeLibraryLoaded) {
            r.add(Status.ERROR, "Native check failed: could not load tracerpidcheck library: " + nativeLibraryLoadError);
            return r.toJson();
        }
        try {
            int tracerPidNative = getTracerPidNative();
            if (tracerPidNative < 0) {
                r.add(Status.ERROR, "Native check failed: could not read or parse TracerPid from /proc/self/status.");
            } else if (tracerPidNative > 0) {
                r.add(Status.FAIL, "Native check: native debugger detected via TracerPid=" + tracerPidNative + " in /proc/self/status.");
            } else {
                r.add(Status.PASS, "Native check: no native debugger detected via TracerPid=" + tracerPidNative + " in /proc/self/status.");
            }
        } catch (Exception e4) {
            r.add(Status.ERROR, "Native check failed: unexpected error while checking TracerPid: " + e4.getMessage());
        }
        try {
            int tracerPidInline = getTracerPidInlineSyscallNative();
            if (tracerPidInline == -2) {
                Status status2 = Status.ERROR;
                String[] SUPPORTED_ABIS = Build.SUPPORTED_ABIS;
                Intrinsics.checkNotNullExpressionValue(SUPPORTED_ABIS, "SUPPORTED_ABIS");
                r.add(status2, "Inline-syscall native check is unsupported on this ABI (" + ArraysKt.joinToString$default(SUPPORTED_ABIS, (CharSequence) null, (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 63, (Object) null) + ").");
            } else if (tracerPidInline < 0) {
                r.add(Status.ERROR, "Inline-syscall native check failed: could not read or parse TracerPid.");
            } else if (tracerPidInline > 0) {
                r.add(Status.FAIL, "Inline-syscall native check: native debugger detected via TracerPid=" + tracerPidInline + " in /proc/self/status.");
            } else {
                r.add(Status.PASS, "Inline-syscall native check: no native debugger detected via TracerPid=" + tracerPidInline + " in /proc/self/status.");
            }
        } catch (Exception e5) {
            r.add(Status.ERROR, "Inline-syscall native check failed: unexpected error while checking TracerPid: " + e5.getMessage());
        }
        try {
            int ptraceSelf = ptraceSelfDetectNative();
            if (ptraceSelf > 0) {
                r.add(Status.FAIL, "Native ptrace self-check: debugger likely detected (child could not PTRACE_SEIZE parent: EPERM).");
            } else if (ptraceSelf == 0) {
                r.add(Status.PASS, "Native ptrace self-check: no ptrace-based debugger detected (child seized parent).");
            } else {
                int errno = -ptraceSelf;
                String errnoName = errnoName(errno);
                r.add(Status.ERROR, "Native ptrace self-check failed: ptrace seize flow returned errno=" + errno + " (" + errnoName + ").");
            }
        } catch (Exception e6) {
            r.add(Status.ERROR, "Native ptrace self-check failed: unexpected error: " + e6.getMessage());
        }
        return r.toJson();
    }

    private final int getTracerPidFromProcStatus() {
        Object element$iv;
        String statusText = FilesKt.readText$default(new File("/proc/self/status"), null, 1, null);
        Sequence $this$firstOrNull$iv = StringsKt.lineSequence(statusText);
        Iterator<String> it = $this$firstOrNull$iv.iterator();
        while (true) {
            if (it.hasNext()) {
                element$iv = it.next();
                String it2 = (String) element$iv;
                if (StringsKt.startsWith$default(it2, "TracerPid:", false, 2, (Object) null)) {
                    break;
                }
            } else {
                element$iv = null;
                break;
            }
        }
        String tracerPidLine = (String) element$iv;
        if (tracerPidLine == null) {
            throw new IllegalStateException("TracerPid line not found in /proc/self/status");
        }
        String value = StringsKt.trim((CharSequence) StringsKt.substringAfter$default(tracerPidLine, ":", (String) null, 2, (Object) null)).toString();
        if (value.length() == 0) {
            throw new IllegalStateException("TracerPid value is empty in /proc/self/status");
        }
        return Integer.parseInt(value);
    }

    private final String errnoName(int errno) {
        return errno == OsConstants.EPERM ? "EPERM" : errno == OsConstants.ESRCH ? "ESRCH" : errno == OsConstants.ECHILD ? "ECHILD" : errno == OsConstants.EBUSY ? "EBUSY" : errno == OsConstants.EINVAL ? "EINVAL" : errno == OsConstants.ENOSYS ? "ENOSYS" : errno == OsConstants.EACCES ? "EACCES" : "UNKNOWN_ERRNO";
    }
}

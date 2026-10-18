package org.owasp.mastestapp;

import a3.i;
import a3.m;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import h1.AbstractC0489n;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import t1.AbstractC0902k;

/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001R\u001a\u0010\u0003\u001a\u00020\u00028\u0006X\u0086D¢\u0006\f\n\u0004\b\u0003\u0010\u0004\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0007"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "", "shouldRunInMainThread", "Z", "getShouldRunInMainThread", "()Z", "app_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class MastgTest {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Context f7310a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f7311b;

    public MastgTest(Context context) {
        AbstractC0902k.e(context, "context");
        this.f7310a = context;
        this.f7311b = AbstractC0489n.I0("com.topjohnwu.magisk", "eu.chainfire.supersu", "me.weishu.kernelsu");
    }

    public final String a() {
        Context context;
        Object next;
        i iVar = new i();
        try {
            Iterator it = this.f7311b.iterator();
            while (true) {
                boolean zHasNext = it.hasNext();
                context = this.f7310a;
                if (!zHasNext) {
                    next = null;
                    break;
                }
                next = it.next();
                String str = (String) next;
                PackageManager packageManager = context.getPackageManager();
                try {
                    if (Build.VERSION.SDK_INT >= 33) {
                        packageManager.getPackageInfo(str, PackageManager.PackageInfoFlags.of(0L));
                    } else {
                        packageManager.getPackageInfo(str, 0);
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                }
            }
            String str2 = (String) next;
            if (str2 != null) {
                String str3 = "Detected root manager package '" + str2 + "'. The app closes when root-related packages are found.";
                Log.w("MASTG-DEMO-0132", str3);
                iVar.a(m.f4493g, str3);
                Activity activity = context instanceof Activity ? (Activity) context : null;
                if (activity != null) {
                    activity.finishAffinity();
                }
            } else {
                iVar.a(m.f4494h, "No monitored root manager package was found.");
            }
            return iVar.d();
        } catch (Exception e3) {
            iVar.a(m.f4495i, e3.toString());
            return iVar.d();
        }
    }

    public final boolean getShouldRunInMainThread() {
        return true;
    }
}

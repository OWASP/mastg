package org.owasp.mastestapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000bJ\u000e\u0010\u000f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\u0010"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "notificationManager", "Landroid/app/NotificationManager;", "getNotificationManager", "()Landroid/app/NotificationManager;", "mastgTest", "", "createNotification", "Landroid/app/Notification;", "sensitiveString", "createNotificationOnChannel", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;
    private final NotificationManager notificationManager;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        Object systemService = this.context.getSystemService("notification");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.app.NotificationManager");
        this.notificationManager = (NotificationManager) systemService;
    }

    public final NotificationManager getNotificationManager() {
        return this.notificationManager;
    }

    public final String mastgTest() {
        Log.d("MASTG-TEST", "Hello from the OWASP MASTG Test app.");
        Notification it = createNotification("Hello from the OWASP MASTG Test app.");
        this.notificationManager.notify(1, it);
        Notification it2 = createNotificationOnChannel("Hello from the OWASP MASTG Test app.");
        this.notificationManager.notify(2, it2);
        return "Hello from the OWASP MASTG Test app.";
    }

    public final Notification createNotification(String sensitiveString) {
        Intrinsics.checkNotNullParameter(sensitiveString, "sensitiveString");
        Notification notificationBuild = new Notification.Builder(this.context).setContentTitle("MASTG Test").setContentText(sensitiveString).build();
        Intrinsics.checkNotNullExpressionValue(notificationBuild, "build(...)");
        return notificationBuild;
    }

    public final Notification createNotificationOnChannel(String sensitiveString) {
        Intrinsics.checkNotNullParameter(sensitiveString, "sensitiveString");
        Notification notificationBuild = new Notification.Builder(this.context, "TEST_CHANNEL_ID").setContentTitle("MASTG Test").setContentText(sensitiveString).build();
        Intrinsics.checkNotNullExpressionValue(notificationBuild, "build(...)");
        return notificationBuild;
    }
}

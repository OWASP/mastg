package org.owasp.mastestapp;

import android.content.Context;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0014\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bJ\u0014\u0010\r\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bR\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "appUpdateManager", "Lcom/google/android/play/core/appupdate/AppUpdateManager;", "checkForUpdate", "", "appUpdateResultLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroidx/activity/result/IntentSenderRequest;", "resumeUpdateIfInProgress", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final AppUpdateManager appUpdateManager;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        AppUpdateManager appUpdateManagerCreate = AppUpdateManagerFactory.create(context);
        Intrinsics.checkNotNullExpressionValue(appUpdateManagerCreate, "create(...)");
        this.appUpdateManager = appUpdateManagerCreate;
    }

    public final void checkForUpdate(final ActivityResultLauncher<IntentSenderRequest> appUpdateResultLauncher) {
        Intrinsics.checkNotNullParameter(appUpdateResultLauncher, "appUpdateResultLauncher");
        Log.d("MastgTest", "Checking for an update...");
        Task appUpdateInfoTask = this.appUpdateManager.getAppUpdateInfo();
        Intrinsics.checkNotNullExpressionValue(appUpdateInfoTask, "getAppUpdateInfo(...)");
        final Function1 function1 = new Function1() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return MastgTest.checkForUpdate$lambda$0(this.f$0, appUpdateResultLauncher, (AppUpdateInfo) obj);
            }
        };
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda1
            @Override // com.google.android.gms.tasks.OnSuccessListener
            public final void onSuccess(Object obj) {
                MastgTest.checkForUpdate$lambda$1(function1, obj);
            }
        }).addOnFailureListener(new OnFailureListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda2
            @Override // com.google.android.gms.tasks.OnFailureListener
            public final void onFailure(Exception exc) {
                MastgTest.checkForUpdate$lambda$2(exc);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void checkForUpdate$lambda$1(Function1 tmp0, Object p0) {
        Intrinsics.checkNotNullParameter(tmp0, "$tmp0");
        tmp0.invoke(p0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit checkForUpdate$lambda$0(MastgTest this$0, ActivityResultLauncher appUpdateResultLauncher, AppUpdateInfo appUpdateInfo) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(appUpdateResultLauncher, "$appUpdateResultLauncher");
        boolean isUpdateAvailable = appUpdateInfo.updateAvailability() == 2;
        boolean isImmediateUpdateAllowed = appUpdateInfo.isUpdateTypeAllowed(1);
        if (!isUpdateAvailable || !isImmediateUpdateAllowed) {
            Log.d("MastgTest", "No immediate update available.");
        } else {
            Log.d("MastgTest", "Immediate update available. Starting flow.");
            this$0.appUpdateManager.startUpdateFlowForResult(appUpdateInfo, appUpdateResultLauncher, AppUpdateOptions.newBuilder(1).build());
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void checkForUpdate$lambda$2(Exception e) {
        Intrinsics.checkNotNullParameter(e, "e");
        Log.e("MastgTest", "Failed to check for updates.", e);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void resumeUpdateIfInProgress$lambda$4(Function1 tmp0, Object p0) {
        Intrinsics.checkNotNullParameter(tmp0, "$tmp0");
        tmp0.invoke(p0);
    }

    public final void resumeUpdateIfInProgress(final ActivityResultLauncher<IntentSenderRequest> appUpdateResultLauncher) {
        Intrinsics.checkNotNullParameter(appUpdateResultLauncher, "appUpdateResultLauncher");
        Task<AppUpdateInfo> appUpdateInfo = this.appUpdateManager.getAppUpdateInfo();
        final Function1 function1 = new Function1() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda3
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return MastgTest.resumeUpdateIfInProgress$lambda$3(this.f$0, appUpdateResultLauncher, (AppUpdateInfo) obj);
            }
        };
        appUpdateInfo.addOnSuccessListener(new OnSuccessListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda4
            @Override // com.google.android.gms.tasks.OnSuccessListener
            public final void onSuccess(Object obj) {
                MastgTest.resumeUpdateIfInProgress$lambda$4(function1, obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit resumeUpdateIfInProgress$lambda$3(MastgTest this$0, ActivityResultLauncher appUpdateResultLauncher, AppUpdateInfo appUpdateInfo) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(appUpdateResultLauncher, "$appUpdateResultLauncher");
        if (appUpdateInfo.updateAvailability() == 3) {
            Log.d("MastgTest", "Resuming in-progress update.");
            this$0.appUpdateManager.startUpdateFlowForResult(appUpdateInfo, appUpdateResultLauncher, AppUpdateOptions.newBuilder(1).build());
        }
        return Unit.INSTANCE;
    }
}

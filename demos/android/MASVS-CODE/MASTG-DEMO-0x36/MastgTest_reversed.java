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
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Unit;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001:\u0001\"B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0014\u0010\u0015\u001a\u00020\u00102\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\f0\u000bJ\u0006\u0010\u0017\u001a\u00020\u0010J\u001e\u0010\u0018\u001a\u00020\u00102\u0006\u0010\u0019\u001a\u00020\u001a2\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002J\u0014\u0010\u001b\u001a\u00020\u00102\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\f0\u000bJ\u001e\u0010\u001c\u001a\u00020\u00102\u0006\u0010\u001d\u001a\u00020\u001e2\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002J\u001e\u0010\u001f\u001a\u00020\u00102\u0006\u0010\u001d\u001a\u00020\u001e2\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002J\u0014\u0010 \u001a\u00020\u00102\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\f0\u000bJ\u0016\u0010!\u001a\u00020\u00102\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0007R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\n\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R(\u0010\r\u001a\u0010\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u0010\u0018\u00010\u000eX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014¨\u0006#"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "appUpdateManager", "Lcom/google/android/play/core/appupdate/AppUpdateManager;", "installStateListener", "Lcom/google/android/play/core/install/InstallStateUpdatedListener;", "pendingUpdateLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroidx/activity/result/IntentSenderRequest;", "onUpdateStateChanged", "Lkotlin/Function1;", "Lorg/owasp/mastestapp/MastgTest$UpdateState;", "", "getOnUpdateStateChanged", "()Lkotlin/jvm/functions/Function1;", "setOnUpdateStateChanged", "(Lkotlin/jvm/functions/Function1;)V", "registerInstallStateListener", "appUpdateResultLauncher", "unregisterInstallStateListener", "handleInstallState", "state", "Lcom/google/android/play/core/install/InstallState;", "checkForUpdate", "handleUpdateAvailability", "appUpdateInfo", "Lcom/google/android/play/core/appupdate/AppUpdateInfo;", "startUpdateFlow", "enforceUpdateOnResume", "resumeUpdateIfInProgress", "UpdateState", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateListener;
    private Function1<? super UpdateState, Unit> onUpdateStateChanged;
    private ActivityResultLauncher<IntentSenderRequest> pendingUpdateLauncher;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        AppUpdateManager appUpdateManagerCreate = AppUpdateManagerFactory.create(context);
        Intrinsics.checkNotNullExpressionValue(appUpdateManagerCreate, "create(...)");
        this.appUpdateManager = appUpdateManagerCreate;
    }

    public final Function1<UpdateState, Unit> getOnUpdateStateChanged() {
        return this.onUpdateStateChanged;
    }

    public final void setOnUpdateStateChanged(Function1<? super UpdateState, Unit> function1) {
        this.onUpdateStateChanged = function1;
    }

    /* compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\n\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n¨\u0006\u000b"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$UpdateState;", "", "<init>", "(Ljava/lang/String;I)V", "CHECKING", "UPDATE_REQUIRED", "UPDATE_IN_PROGRESS", "UPDATE_CANCELED", "UPDATE_FAILED", "NO_UPDATE_AVAILABLE", "UPDATE_INSTALLED", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public enum UpdateState {
        CHECKING,
        UPDATE_REQUIRED,
        UPDATE_IN_PROGRESS,
        UPDATE_CANCELED,
        UPDATE_FAILED,
        NO_UPDATE_AVAILABLE,
        UPDATE_INSTALLED;

        private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);

        public static EnumEntries<UpdateState> getEntries() {
            return $ENTRIES;
        }
    }

    public final void registerInstallStateListener(final ActivityResultLauncher<IntentSenderRequest> appUpdateResultLauncher) {
        Intrinsics.checkNotNullParameter(appUpdateResultLauncher, "appUpdateResultLauncher");
        this.pendingUpdateLauncher = appUpdateResultLauncher;
        this.installStateListener = new InstallStateUpdatedListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda0
            @Override // com.google.android.play.core.listener.StateUpdatedListener
            public final void onStateUpdate(InstallState installState) {
                MastgTest.registerInstallStateListener$lambda$0(this.f$0, appUpdateResultLauncher, installState);
            }
        };
        AppUpdateManager appUpdateManager = this.appUpdateManager;
        InstallStateUpdatedListener installStateUpdatedListener = this.installStateListener;
        Intrinsics.checkNotNull(installStateUpdatedListener);
        appUpdateManager.registerListener(installStateUpdatedListener);
        Log.d("MastgTest", "InstallStateUpdatedListener registered.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void registerInstallStateListener$lambda$0(MastgTest this$0, ActivityResultLauncher appUpdateResultLauncher, InstallState state) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(appUpdateResultLauncher, "$appUpdateResultLauncher");
        Intrinsics.checkNotNullParameter(state, "state");
        this$0.handleInstallState(state, appUpdateResultLauncher);
    }

    public final void unregisterInstallStateListener() {
        InstallStateUpdatedListener it = this.installStateListener;
        if (it != null) {
            this.appUpdateManager.unregisterListener(it);
            Log.d("MastgTest", "InstallStateUpdatedListener unregistered.");
        }
        this.installStateListener = null;
        this.pendingUpdateLauncher = null;
    }

    private final void handleInstallState(InstallState state, ActivityResultLauncher<IntentSenderRequest> appUpdateResultLauncher) {
        switch (state.installStatus()) {
            case 0:
                Log.d("MastgTest", "Update status unknown.");
                break;
            case 1:
                Log.d("MastgTest", "Update pending...");
                Function1<? super UpdateState, Unit> function1 = this.onUpdateStateChanged;
                if (function1 != null) {
                    function1.invoke(UpdateState.UPDATE_IN_PROGRESS);
                    break;
                }
                break;
            case 2:
                Log.d("MastgTest", "Update downloading: " + state.bytesDownloaded() + "/" + state.totalBytesToDownload());
                Function1<? super UpdateState, Unit> function12 = this.onUpdateStateChanged;
                if (function12 != null) {
                    function12.invoke(UpdateState.UPDATE_IN_PROGRESS);
                    break;
                }
                break;
            case 3:
                Log.d("MastgTest", "Update installing...");
                Function1<? super UpdateState, Unit> function13 = this.onUpdateStateChanged;
                if (function13 != null) {
                    function13.invoke(UpdateState.UPDATE_IN_PROGRESS);
                    break;
                }
                break;
            case 4:
                Log.d("MastgTest", "Update installed successfully.");
                Function1<? super UpdateState, Unit> function14 = this.onUpdateStateChanged;
                if (function14 != null) {
                    function14.invoke(UpdateState.UPDATE_INSTALLED);
                    break;
                }
                break;
            case 5:
                Log.e("MastgTest", "Update FAILED. Re-triggering mandatory update.");
                Function1<? super UpdateState, Unit> function15 = this.onUpdateStateChanged;
                if (function15 != null) {
                    function15.invoke(UpdateState.UPDATE_FAILED);
                }
                checkForUpdate(appUpdateResultLauncher);
                break;
            case 6:
                Log.w("MastgTest", "Update was CANCELED by user. Re-triggering mandatory update.");
                Function1<? super UpdateState, Unit> function16 = this.onUpdateStateChanged;
                if (function16 != null) {
                    function16.invoke(UpdateState.UPDATE_CANCELED);
                }
                checkForUpdate(appUpdateResultLauncher);
                break;
            case 10:
                Log.d("MastgTest", "Update requires UI intent. Re-triggering update flow.");
                Function1<? super UpdateState, Unit> function17 = this.onUpdateStateChanged;
                if (function17 != null) {
                    function17.invoke(UpdateState.UPDATE_REQUIRED);
                }
                checkForUpdate(appUpdateResultLauncher);
                break;
            case 11:
                Log.d("MastgTest", "Update downloaded, completing installation...");
                this.appUpdateManager.completeUpdate();
                break;
        }
    }

    public final void checkForUpdate(final ActivityResultLauncher<IntentSenderRequest> appUpdateResultLauncher) {
        Intrinsics.checkNotNullParameter(appUpdateResultLauncher, "appUpdateResultLauncher");
        Log.d("MastgTest", "Checking for an update...");
        Function1<? super UpdateState, Unit> function1 = this.onUpdateStateChanged;
        if (function1 != null) {
            function1.invoke(UpdateState.CHECKING);
        }
        Task appUpdateInfoTask = this.appUpdateManager.getAppUpdateInfo();
        Intrinsics.checkNotNullExpressionValue(appUpdateInfoTask, "getAppUpdateInfo(...)");
        final Function1 function12 = new Function1() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda1
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return MastgTest.checkForUpdate$lambda$2(this.f$0, appUpdateResultLauncher, (AppUpdateInfo) obj);
            }
        };
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda2
            @Override // com.google.android.gms.tasks.OnSuccessListener
            public final void onSuccess(Object obj) {
                MastgTest.checkForUpdate$lambda$3(function12, obj);
            }
        }).addOnFailureListener(new OnFailureListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda3
            @Override // com.google.android.gms.tasks.OnFailureListener
            public final void onFailure(Exception exc) {
                MastgTest.checkForUpdate$lambda$4(this.f$0, exc);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void checkForUpdate$lambda$3(Function1 tmp0, Object p0) {
        Intrinsics.checkNotNullParameter(tmp0, "$tmp0");
        tmp0.invoke(p0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit checkForUpdate$lambda$2(MastgTest this$0, ActivityResultLauncher appUpdateResultLauncher, AppUpdateInfo appUpdateInfo) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(appUpdateResultLauncher, "$appUpdateResultLauncher");
        Intrinsics.checkNotNull(appUpdateInfo);
        this$0.handleUpdateAvailability(appUpdateInfo, appUpdateResultLauncher);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void checkForUpdate$lambda$4(MastgTest this$0, Exception e) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(e, "e");
        Log.e("MastgTest", "Failed to check for updates.", e);
        Function1<? super UpdateState, Unit> function1 = this$0.onUpdateStateChanged;
        if (function1 != null) {
            function1.invoke(UpdateState.NO_UPDATE_AVAILABLE);
        }
    }

    private final void handleUpdateAvailability(AppUpdateInfo appUpdateInfo, ActivityResultLauncher<IntentSenderRequest> appUpdateResultLauncher) {
        int updateAvailability = appUpdateInfo.updateAvailability();
        boolean isImmediateUpdateAllowed = appUpdateInfo.isUpdateTypeAllowed(1);
        Log.d("MastgTest", "Update availability: " + updateAvailability + ", Immediate allowed: " + isImmediateUpdateAllowed);
        switch (updateAvailability) {
            case 0:
                Log.d("MastgTest", "Update availability unknown.");
                Function1<? super UpdateState, Unit> function1 = this.onUpdateStateChanged;
                if (function1 != null) {
                    function1.invoke(UpdateState.NO_UPDATE_AVAILABLE);
                    break;
                }
                break;
            case 1:
                Log.d("MastgTest", "No update available.");
                Function1<? super UpdateState, Unit> function12 = this.onUpdateStateChanged;
                if (function12 != null) {
                    function12.invoke(UpdateState.NO_UPDATE_AVAILABLE);
                    break;
                }
                break;
            case 2:
                if (isImmediateUpdateAllowed) {
                    Log.d("MastgTest", "Immediate update available. Starting flow.");
                    Function1<? super UpdateState, Unit> function13 = this.onUpdateStateChanged;
                    if (function13 != null) {
                        function13.invoke(UpdateState.UPDATE_REQUIRED);
                    }
                    startUpdateFlow(appUpdateInfo, appUpdateResultLauncher);
                    break;
                } else {
                    Log.d("MastgTest", "Update available but IMMEDIATE not allowed.");
                    Function1<? super UpdateState, Unit> function14 = this.onUpdateStateChanged;
                    if (function14 != null) {
                        function14.invoke(UpdateState.NO_UPDATE_AVAILABLE);
                        break;
                    }
                }
                break;
            case 3:
                Log.d("MastgTest", "Update already in progress. Resuming flow.");
                Function1<? super UpdateState, Unit> function15 = this.onUpdateStateChanged;
                if (function15 != null) {
                    function15.invoke(UpdateState.UPDATE_IN_PROGRESS);
                }
                startUpdateFlow(appUpdateInfo, appUpdateResultLauncher);
                break;
        }
    }

    private final void startUpdateFlow(AppUpdateInfo appUpdateInfo, ActivityResultLauncher<IntentSenderRequest> appUpdateResultLauncher) {
        boolean started = this.appUpdateManager.startUpdateFlowForResult(appUpdateInfo, appUpdateResultLauncher, AppUpdateOptions.newBuilder(1).build());
        if (started) {
            Log.d("MastgTest", "Update flow started successfully.");
            return;
        }
        Log.e("MastgTest", "Failed to start update flow.");
        Function1<? super UpdateState, Unit> function1 = this.onUpdateStateChanged;
        if (function1 != null) {
            function1.invoke(UpdateState.UPDATE_FAILED);
        }
    }

    public final void enforceUpdateOnResume(final ActivityResultLauncher<IntentSenderRequest> appUpdateResultLauncher) {
        Intrinsics.checkNotNullParameter(appUpdateResultLauncher, "appUpdateResultLauncher");
        Log.d("MastgTest", "onResume: Checking for pending mandatory updates...");
        Task<AppUpdateInfo> appUpdateInfo = this.appUpdateManager.getAppUpdateInfo();
        final Function1 function1 = new Function1() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda4
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return MastgTest.enforceUpdateOnResume$lambda$5(this.f$0, appUpdateResultLauncher, (AppUpdateInfo) obj);
            }
        };
        appUpdateInfo.addOnSuccessListener(new OnSuccessListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda5
            @Override // com.google.android.gms.tasks.OnSuccessListener
            public final void onSuccess(Object obj) {
                MastgTest.enforceUpdateOnResume$lambda$6(function1, obj);
            }
        }).addOnFailureListener(new OnFailureListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda6
            @Override // com.google.android.gms.tasks.OnFailureListener
            public final void onFailure(Exception exc) {
                MastgTest.enforceUpdateOnResume$lambda$7(exc);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void enforceUpdateOnResume$lambda$6(Function1 tmp0, Object p0) {
        Intrinsics.checkNotNullParameter(tmp0, "$tmp0");
        tmp0.invoke(p0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit enforceUpdateOnResume$lambda$5(MastgTest this$0, ActivityResultLauncher appUpdateResultLauncher, AppUpdateInfo appUpdateInfo) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(appUpdateResultLauncher, "$appUpdateResultLauncher");
        int updateAvailability = appUpdateInfo.updateAvailability();
        boolean isImmediateAllowed = appUpdateInfo.isUpdateTypeAllowed(1);
        Log.d("MastgTest", "onResume check - Availability: " + updateAvailability + ", Immediate allowed: " + isImmediateAllowed);
        switch (updateAvailability) {
            case 0:
                Log.d("MastgTest", "onResume: Update availability unknown, checking again...");
                this$0.checkForUpdate(appUpdateResultLauncher);
                break;
            case 1:
                Log.d("MastgTest", "onResume: No update required.");
                Function1<? super UpdateState, Unit> function1 = this$0.onUpdateStateChanged;
                if (function1 != null) {
                    function1.invoke(UpdateState.NO_UPDATE_AVAILABLE);
                    break;
                }
                break;
            case 2:
                if (isImmediateAllowed) {
                    Log.w("MastgTest", "onResume: Update still available but not started. Re-enforcing mandatory update.");
                    Function1<? super UpdateState, Unit> function12 = this$0.onUpdateStateChanged;
                    if (function12 != null) {
                        function12.invoke(UpdateState.UPDATE_REQUIRED);
                    }
                    Intrinsics.checkNotNull(appUpdateInfo);
                    this$0.startUpdateFlow(appUpdateInfo, appUpdateResultLauncher);
                    break;
                }
                break;
            case 3:
                Log.d("MastgTest", "onResume: Resuming in-progress update.");
                Function1<? super UpdateState, Unit> function13 = this$0.onUpdateStateChanged;
                if (function13 != null) {
                    function13.invoke(UpdateState.UPDATE_IN_PROGRESS);
                }
                Intrinsics.checkNotNull(appUpdateInfo);
                this$0.startUpdateFlow(appUpdateInfo, appUpdateResultLauncher);
                break;
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void enforceUpdateOnResume$lambda$7(Exception e) {
        Intrinsics.checkNotNullParameter(e, "e");
        Log.e("MastgTest", "onResume: Failed to check update status.", e);
    }

    @Deprecated(message = "Use enforceUpdateOnResume() for comprehensive bypass prevention", replaceWith = @ReplaceWith(expression = "enforceUpdateOnResume(appUpdateResultLauncher)", imports = {}))
    public final void resumeUpdateIfInProgress(ActivityResultLauncher<IntentSenderRequest> appUpdateResultLauncher) {
        Intrinsics.checkNotNullParameter(appUpdateResultLauncher, "appUpdateResultLauncher");
        enforceUpdateOnResume(appUpdateResultLauncher);
    }
}

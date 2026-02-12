package org.owasp.mastestapp;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.concurrent.CountDownLatch;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\f\u001a\u00020\rH\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u00020\u0007X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "shouldRunInMainThread", "", "getShouldRunInMainThread", "()Z", "mainHandler", "Landroid/os/Handler;", "mastgTest", "", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;
    private final Handler mainHandler;
    private final boolean shouldRunInMainThread;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public final boolean getShouldRunInMainThread() {
        return this.shouldRunInMainThread;
    }

    public final String mastgTest() {
        final DemoResults results = new DemoResults("0083");
        final CountDownLatch latch1 = new CountDownLatch(1);
        final Ref.ObjectRef authResult1 = new Ref.ObjectRef();
        final BiometricPrompt prompt1 = new BiometricPrompt.Builder(this.context).setTitle("Test 1: Device Credential").setSubtitle("Using DEVICE_CREDENTIAL (Security: FAIL)").setDescription("This allows also PIN/pattern/password authentication").setAllowedAuthenticators(32783).setDeviceCredentialAllowed(true).build();
        Intrinsics.checkNotNullExpressionValue(prompt1, "build(...)");
        this.mainHandler.post(new Runnable() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MastgTest.mastgTest$lambda$0(prompt1, this, authResult1, results, latch1);
            }
        });
        latch1.await();
        final CountDownLatch latch2 = new CountDownLatch(1);
        final Ref.ObjectRef authResult2 = new Ref.ObjectRef();
        final BiometricPrompt prompt2 = new BiometricPrompt.Builder(this.context).setTitle("Test 2: Biometric Strong").setSubtitle("Using BIOMETRIC_STRONG (Security: PASS)").setDescription("This only allows Class 3 biometrics").setAllowedAuthenticators(15).setNegativeButton("Cancel", this.context.getMainExecutor(), new DialogInterface.OnClickListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                MastgTest.mastgTest$lambda$1(Ref.ObjectRef.this, latch2, dialogInterface, i);
            }
        }).build();
        Intrinsics.checkNotNullExpressionValue(prompt2, "build(...)");
        this.mainHandler.post(new Runnable() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MastgTest.mastgTest$lambda$2(prompt2, this, authResult2, results, latch2);
            }
        });
        latch2.await();
        return results.toJson();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void mastgTest$lambda$0(BiometricPrompt prompt1, MastgTest this$0, final Ref.ObjectRef authResult1, final DemoResults results, final CountDownLatch latch1) {
        Intrinsics.checkNotNullParameter(prompt1, "$prompt1");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(authResult1, "$authResult1");
        Intrinsics.checkNotNullParameter(results, "$results");
        Intrinsics.checkNotNullParameter(latch1, "$latch1");
        prompt1.authenticate(new CancellationSignal(), this$0.context.getMainExecutor(), new BiometricPrompt.AuthenticationCallback() { // from class: org.owasp.mastestapp.MastgTest$mastgTest$1$1
            @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                Intrinsics.checkNotNullParameter(result, "result");
                Log.d("MASTG-TEST", "DEVICE_CREDENTIAL authentication succeeded");
                authResult1.element = "User can authenticate with DEVICE_CREDENTIAL (PIN/pattern/password)";
                results.add(Status.FAIL, ((Object) authResult1.element) + ". \n🔓 AUTH - Success!\n⚠️ Allows also PIN/Pattern/Password\n⚠️ Uses DEVICE_CREDENTIAL fallback\n");
                latch1.countDown();
            }

            @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
            public void onAuthenticationFailed() {
                Log.d("MASTG-TEST", "DEVICE_CREDENTIAL authentication failed");
                authResult1.element = "Authentication attempt failed";
                results.add(Status.FAIL, ((Object) authResult1.element) + ". \n⚠️ AUTH - Failed\n⚠️ Allows PIN/Pattern/Password\n⚠️ Uses DEVICE_CREDENTIAL fallback\n");
                latch1.countDown();
            }

            /* JADX WARN: Type inference failed for: r1v8, types: [T, java.lang.String] */
            @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                Intrinsics.checkNotNullParameter(errString, "errString");
                Log.d("MASTG-TEST", "DEVICE_CREDENTIAL authentication error: " + ((Object) errString));
                authResult1.element = "Authentication error: " + ((Object) errString) + " (code: " + errorCode + ")";
                results.add(Status.ERROR, ((Object) authResult1.element) + ". \n⚠️ AUTH - Error\n⚠️ Allows PIN/Pattern/Password\n⚠️ Uses DEVICE_CREDENTIAL fallback\n");
                latch1.countDown();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void mastgTest$lambda$1(Ref.ObjectRef authResult2, CountDownLatch latch2, DialogInterface dialogInterface, int i) {
        Intrinsics.checkNotNullParameter(authResult2, "$authResult2");
        Intrinsics.checkNotNullParameter(latch2, "$latch2");
        Log.d("MASTG-TEST", "BIOMETRIC_STRONG authentication cancelled");
        authResult2.element = "User cancelled authentication";
        latch2.countDown();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void mastgTest$lambda$2(BiometricPrompt prompt2, MastgTest this$0, final Ref.ObjectRef authResult2, final DemoResults results, final CountDownLatch latch2) {
        Intrinsics.checkNotNullParameter(prompt2, "$prompt2");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(authResult2, "$authResult2");
        Intrinsics.checkNotNullParameter(results, "$results");
        Intrinsics.checkNotNullParameter(latch2, "$latch2");
        prompt2.authenticate(new CancellationSignal(), this$0.context.getMainExecutor(), new BiometricPrompt.AuthenticationCallback() { // from class: org.owasp.mastestapp.MastgTest$mastgTest$2$1
            @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                Intrinsics.checkNotNullParameter(result, "result");
                Log.d("MASTG-TEST", "BIOMETRIC_STRONG authentication succeeded");
                authResult2.element = "User authenticated with BIOMETRIC_STRONG";
                results.add(Status.PASS, ((Object) authResult2.element) + ". \n🔓 AUTH - Success!\n✅ Allows only Strong Biometric\n\nThis configuration is secure because it only allows Class 3 biometrics.");
                latch2.countDown();
            }

            @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
            public void onAuthenticationFailed() {
                Log.d("MASTG-TEST", "BIOMETRIC_STRONG authentication failed");
                authResult2.element = "Authentication attempt failed (biometric not recognized)";
                results.add(Status.FAIL, ((Object) authResult2.element) + ". \n⚠️ AUTH - Failed\n");
                latch2.countDown();
            }

            /* JADX WARN: Type inference failed for: r1v8, types: [T, java.lang.String] */
            @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                Intrinsics.checkNotNullParameter(errString, "errString");
                Log.d("MASTG-TEST", "BIOMETRIC_STRONG authentication error: " + ((Object) errString));
                authResult2.element = "Authentication error: " + ((Object) errString) + " (code: " + errorCode + ")";
                results.add(Status.ERROR, ((Object) authResult2.element) + ". \n⚠️ AUTH - Error\n");
                latch2.countDown();
            }
        });
    }
}
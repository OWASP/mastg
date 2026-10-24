package org.owasp.mastestapp;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Log;
import java.security.Key;
import java.security.KeyStore;
import java.util.concurrent.CountDownLatch;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.text.Charsets;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u0013\u001a\u00020\u0014H\u0002J\b\u0010\u0015\u001a\u00020\u0016H\u0002J\b\u0010\u0017\u001a\u00020\u0018H\u0002J\b\u0010\u0019\u001a\u00020\u0018H\u0002J\b\u0010\u001a\u001a\u00020\rH\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u00020\u0007X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\rX\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\rX\u0082D¢\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0011X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001b"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "shouldRunInMainThread", "", "getShouldRunInMainThread", "()Z", "mainHandler", "Landroid/os/Handler;", "secretToken", "", "KEY_NAME", "ANDROID_KEYSTORE", "encryptedToken", "", "encryptionIv", "generateSecretKey", "", "getSecretKey", "Ljavax/crypto/SecretKey;", "getCipherForEncryption", "Ljavax/crypto/Cipher;", "getCipherForDecryption", "mastgTest", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final String ANDROID_KEYSTORE;
    private final String KEY_NAME;
    private final Context context;
    private byte[] encryptedToken;
    private byte[] encryptionIv;
    private final Handler mainHandler;
    private final String secretToken;
    private final boolean shouldRunInMainThread;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.secretToken = "7xK9mP2qR5tY8wZ3aB6cD0eF";
        this.KEY_NAME = "biometric_secret_key";
        this.ANDROID_KEYSTORE = "AndroidKeyStore";
    }

    public final boolean getShouldRunInMainThread() {
        return this.shouldRunInMainThread;
    }

    private final void generateSecretKey() {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", this.ANDROID_KEYSTORE);
        keyGenerator.init(new KeyGenParameterSpec.Builder(this.KEY_NAME, 3).setBlockModes("GCM").setEncryptionPaddings("NoPadding").setUserAuthenticationRequired(false).setInvalidatedByBiometricEnrollment(false).setUserAuthenticationParameters(86400, 0).setUserAuthenticationValidityDurationSeconds(86400).build());
        keyGenerator.generateKey();
    }

    private final SecretKey getSecretKey() {
        KeyStore keyStore = KeyStore.getInstance(this.ANDROID_KEYSTORE);
        keyStore.load(null);
        Key key = keyStore.getKey(this.KEY_NAME, null);
        Intrinsics.checkNotNull(key, "null cannot be cast to non-null type javax.crypto.SecretKey");
        return (SecretKey) key;
    }

    private final Cipher getCipherForEncryption() {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(1, getSecretKey());
        Intrinsics.checkNotNull(cipher);
        return cipher;
    }

    private final Cipher getCipherForDecryption() {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(2, getSecretKey(), new GCMParameterSpec(128, this.encryptionIv));
        Intrinsics.checkNotNull(cipher);
        return cipher;
    }

    public final String mastgTest() {
        final DemoResults results = new DemoResults("0084");
        final CountDownLatch latch1 = new CountDownLatch(1);
        final Ref.ObjectRef authResult1 = new Ref.ObjectRef();
        final BiometricPrompt prompt1 = new BiometricPrompt.Builder(this.context).setTitle("Test 1: No CryptoObject").setSubtitle("Insecure: No cryptographic binding").setDescription("Secret returned directly without CryptoObject protection").setAllowedAuthenticators(32783).setConfirmationRequired(false).build();
        Intrinsics.checkNotNullExpressionValue(prompt1, "build(...)");
        this.mainHandler.post(new Runnable() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MastgTest.mastgTest$lambda$0(prompt1, this, authResult1, results, latch1);
            }
        });
        latch1.await();
        generateSecretKey();
        final CountDownLatch latchEncrypt = new CountDownLatch(1);
        final Ref.BooleanRef encryptionSucceeded = new Ref.BooleanRef();
        Cipher encryptCipher = getCipherForEncryption();
        final BiometricPrompt.CryptoObject encryptCryptoObject = new BiometricPrompt.CryptoObject(encryptCipher);
        final BiometricPrompt promptEncrypt = new BiometricPrompt.Builder(this.context).setTitle("Test 2a: Encrypt with CryptoObject").setSubtitle("Secure: BIOMETRIC_STRONG required").setDescription("Biometric required to encrypt the secret token").setAllowedAuthenticators(15).setNegativeButton("Cancel", this.context.getMainExecutor(), new DialogInterface.OnClickListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                MastgTest.mastgTest$lambda$1(latchEncrypt, dialogInterface, i);
            }
        }).build();
        Intrinsics.checkNotNullExpressionValue(promptEncrypt, "build(...)");
        this.mainHandler.post(new Runnable() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MastgTest.mastgTest$lambda$2(promptEncrypt, encryptCryptoObject, this, encryptionSucceeded, results, latchEncrypt);
            }
        });
        latchEncrypt.await();
        if (encryptionSucceeded.element && this.encryptedToken != null && this.encryptionIv != null) {
            final CountDownLatch latchDecrypt = new CountDownLatch(1);
            Cipher decryptCipher = getCipherForDecryption();
            final BiometricPrompt.CryptoObject decryptCryptoObject = new BiometricPrompt.CryptoObject(decryptCipher);
            final BiometricPrompt promptDecrypt = new BiometricPrompt.Builder(this.context).setTitle("Test 2b: Decrypt with CryptoObject").setSubtitle("Secure: BIOMETRIC_STRONG required").setDescription("Biometric required to decrypt the secret token").setAllowedAuthenticators(15).setNegativeButton("Cancel", this.context.getMainExecutor(), new DialogInterface.OnClickListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda3
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    MastgTest.mastgTest$lambda$3(latchDecrypt, dialogInterface, i);
                }
            }).build();
            Intrinsics.checkNotNullExpressionValue(promptDecrypt, "build(...)");
            this.mainHandler.post(new Runnable() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    MastgTest.mastgTest$lambda$4(promptDecrypt, decryptCryptoObject, this, results, latchDecrypt);
                }
            });
            latchDecrypt.await();
        }
        return results.toJson();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void mastgTest$lambda$0(BiometricPrompt prompt1, final MastgTest this$0, final Ref.ObjectRef authResult1, final DemoResults results, final CountDownLatch latch1) {
        Intrinsics.checkNotNullParameter(prompt1, "$prompt1");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(authResult1, "$authResult1");
        Intrinsics.checkNotNullParameter(results, "$results");
        Intrinsics.checkNotNullParameter(latch1, "$latch1");
        prompt1.authenticate(new CancellationSignal(), this$0.context.getMainExecutor(), new BiometricPrompt.AuthenticationCallback() { // from class: org.owasp.mastestapp.MastgTest$mastgTest$1$1
            @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                String str;
                Intrinsics.checkNotNullParameter(result, "result");
                Log.d("MASTG-TEST", "Test 1: Auth succeeded (no CryptoObject)");
                authResult1.element = "Authenticated without CryptoObject";
                DemoResults demoResults = results;
                Status status = Status.FAIL;
                str = this$0.secretToken;
                demoResults.add(status, "Secret Token: " + str + "\n\n🔓 AUTH - Success!\n⚠️ No CryptoObject used - secret token returned directly\n⚠️ Allows DEVICE_CREDENTIAL fallback\n");
                latch1.countDown();
            }

            @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
            public void onAuthenticationFailed() {
                Log.d("MASTG-TEST", "Test 1: Auth failed (biometric not recognized)");
                authResult1.element = "Authentication attempt failed";
                results.add(Status.FAIL, ((Object) authResult1.element) + "\n\n⚠️ AUTH - Failed\n");
                latch1.countDown();
            }

            /* JADX WARN: Type inference failed for: r1v8, types: [T, java.lang.String] */
            @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                Intrinsics.checkNotNullParameter(errString, "errString");
                Log.d("MASTG-TEST", "Test 1: Auth error - " + ((Object) errString));
                authResult1.element = "Authentication error: " + ((Object) errString) + " (code: " + errorCode + ")";
                results.add(Status.ERROR, ((Object) authResult1.element) + "\n\n⚠️ AUTH - Error\n");
                latch1.countDown();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void mastgTest$lambda$1(CountDownLatch latchEncrypt, DialogInterface dialogInterface, int i) {
        Intrinsics.checkNotNullParameter(latchEncrypt, "$latchEncrypt");
        Log.d("MASTG-TEST", "Test 2a: Encryption cancelled by user");
        latchEncrypt.countDown();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void mastgTest$lambda$2(BiometricPrompt promptEncrypt, BiometricPrompt.CryptoObject encryptCryptoObject, final MastgTest this$0, final Ref.BooleanRef encryptionSucceeded, final DemoResults results, final CountDownLatch latchEncrypt) {
        Intrinsics.checkNotNullParameter(promptEncrypt, "$promptEncrypt");
        Intrinsics.checkNotNullParameter(encryptCryptoObject, "$encryptCryptoObject");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(encryptionSucceeded, "$encryptionSucceeded");
        Intrinsics.checkNotNullParameter(results, "$results");
        Intrinsics.checkNotNullParameter(latchEncrypt, "$latchEncrypt");
        promptEncrypt.authenticate(encryptCryptoObject, new CancellationSignal(), this$0.context.getMainExecutor(), new BiometricPrompt.AuthenticationCallback() { // from class: org.owasp.mastestapp.MastgTest$mastgTest$2$1
            @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                String str;
                Intrinsics.checkNotNullParameter(result, "result");
                Log.d("MASTG-TEST", "Test 2a: Biometric auth succeeded, encrypting token");
                try {
                    BiometricPrompt.CryptoObject cryptoObject = result.getCryptoObject();
                    byte[] bArr = null;
                    Cipher authenticatedCipher = cryptoObject != null ? cryptoObject.getCipher() : null;
                    MastgTest.this.encryptionIv = authenticatedCipher != null ? authenticatedCipher.getIV() : null;
                    MastgTest mastgTest = MastgTest.this;
                    if (authenticatedCipher != null) {
                        str = MastgTest.this.secretToken;
                        byte[] bytes = str.getBytes(Charsets.UTF_8);
                        Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
                        bArr = authenticatedCipher.doFinal(bytes);
                    }
                    mastgTest.encryptedToken = bArr;
                    encryptionSucceeded.element = true;
                    Log.d("MASTG-TEST", "Test 2a: Token encrypted successfully with CryptoObject");
                } catch (Exception e) {
                    Log.e("MASTG-TEST", "Test 2a: Encryption failed - " + e.getMessage(), e);
                    results.add(Status.ERROR, "Test 2a: Encryption failed: " + e.getMessage() + "\n");
                }
                latchEncrypt.countDown();
            }

            @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
            public void onAuthenticationFailed() {
                Log.d("MASTG-TEST", "Test 2a: Biometric not recognized");
                latchEncrypt.countDown();
            }

            @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                Intrinsics.checkNotNullParameter(errString, "errString");
                Log.d("MASTG-TEST", "Test 2a: Auth error - " + ((Object) errString));
                results.add(Status.ERROR, "Test 2a: Auth error: " + ((Object) errString) + " (code: " + errorCode + ")\n");
                latchEncrypt.countDown();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void mastgTest$lambda$3(CountDownLatch latchDecrypt, DialogInterface dialogInterface, int i) {
        Intrinsics.checkNotNullParameter(latchDecrypt, "$latchDecrypt");
        Log.d("MASTG-TEST", "Test 2b: Decryption cancelled by user");
        latchDecrypt.countDown();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void mastgTest$lambda$4(BiometricPrompt promptDecrypt, BiometricPrompt.CryptoObject decryptCryptoObject, final MastgTest this$0, final DemoResults results, final CountDownLatch latchDecrypt) {
        Intrinsics.checkNotNullParameter(promptDecrypt, "$promptDecrypt");
        Intrinsics.checkNotNullParameter(decryptCryptoObject, "$decryptCryptoObject");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(results, "$results");
        Intrinsics.checkNotNullParameter(latchDecrypt, "$latchDecrypt");
        promptDecrypt.authenticate(decryptCryptoObject, new CancellationSignal(), this$0.context.getMainExecutor(), new BiometricPrompt.AuthenticationCallback() { // from class: org.owasp.mastestapp.MastgTest$mastgTest$3$1
            @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                byte[] decryptedBytes;
                byte[] bArr;
                Intrinsics.checkNotNullParameter(result, "result");
                Log.d("MASTG-TEST", "Test 2b: Biometric auth succeeded, decrypting token");
                try {
                    BiometricPrompt.CryptoObject cryptoObject = result.getCryptoObject();
                    Cipher authenticatedCipher = cryptoObject != null ? cryptoObject.getCipher() : null;
                    if (authenticatedCipher != null) {
                        bArr = MastgTest.this.encryptedToken;
                        decryptedBytes = authenticatedCipher.doFinal(bArr);
                    } else {
                        decryptedBytes = null;
                    }
                    String decryptedToken = decryptedBytes != null ? new String(decryptedBytes, Charsets.UTF_8) : null;
                    Log.d("MASTG-TEST", "Test 2b: Token decrypted successfully with CryptoObject");
                    results.add(Status.FAIL, "Decrypted Secret Token: " + decryptedToken + "\n\n🔓 AUTH - Success!\n✅ Uses CryptoObject for encryption and decryption operations\n✅ BIOMETRIC_STRONG required for all crypto operations\n⚠️ Key created with setUserAuthenticationRequired(false)\n⚠️ Key created with setInvalidatedByBiometricEnrollment(false)\n\nCrypto operations are bound to biometric authentication, but it is not implemented according to security best practices.");
                } catch (Exception e) {
                    Log.e("MASTG-TEST", "Test 2b: Decryption failed - " + e.getMessage(), e);
                    results.add(Status.ERROR, "Test 2b: Decryption failed: " + e.getMessage() + "\n\n⚠️ AUTH succeeded but decryption failed\n");
                }
                latchDecrypt.countDown();
            }

            @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
            public void onAuthenticationFailed() {
                Log.d("MASTG-TEST", "Test 2b: Biometric not recognized");
                results.add(Status.FAIL, "Test 2b: Biometric not recognized\n\n⚠️ AUTH - Failed\n");
                latchDecrypt.countDown();
            }

            @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                Intrinsics.checkNotNullParameter(errString, "errString");
                Log.d("MASTG-TEST", "Test 2b: Auth error - " + ((Object) errString));
                results.add(Status.ERROR, "Test 2b: Auth error: " + ((Object) errString) + " (code: " + errorCode + ")\n\n⚠️ AUTH - Error\n");
                latchDecrypt.countDown();
            }
        });
    }
}
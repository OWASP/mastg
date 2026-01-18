package org.owasp.mastestapp;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Log;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import java.security.Key;
import java.security.KeyStore;
import java.util.concurrent.Executor;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000 \u001e2\u00020\u0001:\u0001\u001eB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001a\u0010\u0013\u001a\u00020\f2\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\r0\u000bJ\b\u0010\u0015\u001a\u00020\rH\u0002J\b\u0010\u0016\u001a\u00020\rH\u0002J\b\u0010\u0017\u001a\u00020\rH\u0002J\b\u0010\u0018\u001a\u00020\u0019H\u0002J\b\u0010\u001a\u001a\u00020\u0019H\u0002J\b\u0010\u001b\u001a\u00020\u001cH\u0002J\b\u0010\u001d\u001a\u00020\rH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u00020\u0007X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u001c\u0010\n\u001a\u0010\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\r\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0011X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001f"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "shouldRunInMainThread", "", "getShouldRunInMainThread", "()Z", "resultCallback", "Lkotlin/Function1;", "", "", "results", "Ljava/lang/StringBuilder;", "encryptedToken", "", "encryptionIv", "mastgTest", "onResult", "showFallbackBiometricPrompt", "showCryptoBasedPrompt", "decryptToken", "generateSecretKey", "Ljavax/crypto/SecretKey;", "getSecretKey", "getCipher", "Ljavax/crypto/Cipher;", "updateUI", "Companion", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    private static final String KEY_NAME = "biometric_key";
    private static final String SECRET_TOKEN = "MySecretToken123";
    private final Context context;
    private byte[] encryptedToken;
    private byte[] encryptionIv;
    private Function1<? super String, Unit> resultCallback;
    private StringBuilder results;
    private final boolean shouldRunInMainThread;
    public static final int $stable = 8;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.shouldRunInMainThread = true;
        this.results = new StringBuilder();
    }

    public final boolean getShouldRunInMainThread() {
        return this.shouldRunInMainThread;
    }

    public final String mastgTest(Function1<? super String, Unit> onResult) {
        Intrinsics.checkNotNullParameter(onResult, "onResult");
        this.resultCallback = onResult;
        this.results = new StringBuilder();
        if (!(this.context instanceof FragmentActivity)) {
            return "Error: Context is not a FragmentActivity";
        }
        BiometricManager biometricManager = BiometricManager.from(this.context);
        Intrinsics.checkNotNullExpressionValue(biometricManager, "from(...)");
        if (biometricManager.canAuthenticate(15) == 0) {
            Log.d("MastgTest", "Biometrics available");
            this.results.append("🔐 Biometric Authentication Demo\n\nTesting two implementations:\n1. With DEVICE_CREDENTIAL fallback\n2. With CryptoObject (encrypt/decrypt)\n\nAuthenticating...\n\n");
            showFallbackBiometricPrompt();
            String sb = this.results.toString();
            Intrinsics.checkNotNullExpressionValue(sb, "toString(...)");
            return sb;
        }
        return "Error: Biometric authentication not available on this device";
    }

    private final void showFallbackBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this.context);
        Intrinsics.checkNotNullExpressionValue(executor, "getMainExecutor(...)");
        Context context = this.context;
        Intrinsics.checkNotNull(context, "null cannot be cast to non-null type androidx.fragment.app.FragmentActivity");
        BiometricPrompt biometricPrompt = new BiometricPrompt((FragmentActivity) context, executor, new MastgTest$showFallbackBiometricPrompt$biometricPrompt$1(this));
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("🔓 1st Biometric Auth").setSubtitle("Allows device credentials").setDescription("This implementation uses DEVICE_CREDENTIAL").setAllowedAuthenticators(32783).setDeviceCredentialAllowed(true).build();
        Intrinsics.checkNotNullExpressionValue(promptInfo, "build(...)");
        biometricPrompt.authenticate(promptInfo);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void showCryptoBasedPrompt() {
        try {
            SecretKey secretKey = getSecretKey();
            Cipher cipher = getCipher();
            cipher.init(1, secretKey);
            Executor executor = ContextCompat.getMainExecutor(this.context);
            Intrinsics.checkNotNullExpressionValue(executor, "getMainExecutor(...)");
            Context context = this.context;
            Intrinsics.checkNotNull(context, "null cannot be cast to non-null type androidx.fragment.app.FragmentActivity");
            BiometricPrompt biometricPrompt = new BiometricPrompt((FragmentActivity) context, executor, new MastgTest$showCryptoBasedPrompt$biometricPrompt$1(this));
            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("🔒 Crypto-Based Auth").setSubtitle("Uses CryptoObject").setDescription("Encrypting token: MySecretToken123").setNegativeButtonText("Cancel").setConfirmationRequired(false).setAllowedAuthenticators(15).build();
            Intrinsics.checkNotNullExpressionValue(promptInfo, "build(...)");
            biometricPrompt.authenticate(promptInfo, new BiometricPrompt.CryptoObject(cipher));
        } catch (Exception e) {
            this.results.append("🔒 CRYPTO AUTH - Setup error: " + e.getMessage() + "\n");
            updateUI();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void decryptToken() {
        byte[] iv = this.encryptionIv;
        if (iv == null) {
            this.results.append("🔓 DECRYPTION - Error: IV is null\n");
            updateUI();
            return;
        }
        try {
            SecretKey secretKey = getSecretKey();
            Cipher cipher = getCipher();
            cipher.init(2, secretKey, new IvParameterSpec(iv));
            Executor executor = ContextCompat.getMainExecutor(this.context);
            Intrinsics.checkNotNullExpressionValue(executor, "getMainExecutor(...)");
            Context context = this.context;
            Intrinsics.checkNotNull(context, "null cannot be cast to non-null type androidx.fragment.app.FragmentActivity");
            BiometricPrompt biometricPrompt = new BiometricPrompt((FragmentActivity) context, executor, new BiometricPrompt.AuthenticationCallback() { // from class: org.owasp.mastestapp.MastgTest$decryptToken$biometricPrompt$1
                @Override // androidx.biometric.BiometricPrompt.AuthenticationCallback
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    StringBuilder sb;
                    StringBuilder sb2;
                    byte[] bArr;
                    byte[] bArr2;
                    StringBuilder sb3;
                    StringBuilder sb4;
                    StringBuilder sb5;
                    StringBuilder sb6;
                    StringBuilder sb7;
                    Intrinsics.checkNotNullParameter(result, "result");
                    try {
                        BiometricPrompt.CryptoObject cryptoObject = result.getCryptoObject();
                        Cipher authenticatedCipher = cryptoObject != null ? cryptoObject.getCipher() : null;
                        if (authenticatedCipher != null) {
                            bArr = MastgTest.this.encryptedToken;
                            if (bArr != null) {
                                bArr2 = MastgTest.this.encryptedToken;
                                byte[] decryptedBytes = authenticatedCipher.doFinal(bArr2);
                                Intrinsics.checkNotNull(decryptedBytes);
                                String decryptedToken = new String(decryptedBytes, Charsets.UTF_8);
                                sb3 = MastgTest.this.results;
                                sb3.append("🔓 DECRYPTION - Success!\n");
                                sb4 = MastgTest.this.results;
                                sb4.append("⚠️  Confirmation not required\n");
                                sb5 = MastgTest.this.results;
                                sb5.append("✅  Token decrypted: " + decryptedToken + "\n");
                                sb6 = MastgTest.this.results;
                                sb6.append("✅  Original token: MySecretToken123\n");
                                sb7 = MastgTest.this.results;
                                sb7.append("✅  Match: " + Intrinsics.areEqual(decryptedToken, "MySecretToken123") + "\n");
                                MastgTest.this.updateUI();
                                return;
                            }
                        }
                        sb2 = MastgTest.this.results;
                        sb2.append("🔓 DECRYPTION - Error: Cipher or encrypted data is null\n");
                        MastgTest.this.updateUI();
                    } catch (Exception e) {
                        sb = MastgTest.this.results;
                        sb.append("🔓 DECRYPTION - Error: " + e.getMessage() + "\n");
                        MastgTest.this.updateUI();
                    }
                }

                @Override // androidx.biometric.BiometricPrompt.AuthenticationCallback
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    StringBuilder sb;
                    Intrinsics.checkNotNullParameter(errString, "errString");
                    super.onAuthenticationError(errorCode, errString);
                    sb = MastgTest.this.results;
                    sb.append("🔓 DECRYPTION - Error: " + ((Object) errString) + "\n");
                    MastgTest.this.updateUI();
                }

                @Override // androidx.biometric.BiometricPrompt.AuthenticationCallback
                public void onAuthenticationFailed() {
                    StringBuilder sb;
                    super.onAuthenticationFailed();
                    sb = MastgTest.this.results;
                    sb.append("🔓 DECRYPTION - Failed\n");
                    MastgTest.this.updateUI();
                }
            });
            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("🔓 Decrypt Token").setSubtitle("Authenticate to decrypt").setDescription("Decrypting the encrypted token").setNegativeButtonText("Cancel").setConfirmationRequired(false).setAllowedAuthenticators(15).build();
            Intrinsics.checkNotNullExpressionValue(promptInfo, "build(...)");
            biometricPrompt.authenticate(promptInfo, new BiometricPrompt.CryptoObject(cipher));
        } catch (Exception e) {
            this.results.append("🔓 DECRYPTION - Setup error: " + e.getMessage() + "\n");
            updateUI();
        }
    }

    private final SecretKey generateSecretKey() {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
        KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(KEY_NAME, 3).setBlockModes("CBC").setEncryptionPaddings("PKCS7Padding").setUserAuthenticationRequired(false).setUserAuthenticationValidityDurationSeconds(86400).setInvalidatedByBiometricEnrollment(false).setUserAuthenticationParameters(86400, 3).build();
        Intrinsics.checkNotNullExpressionValue(keyGenParameterSpec, "build(...)");
        keyGenerator.init(keyGenParameterSpec);
        SecretKey generateKey = keyGenerator.generateKey();
        Intrinsics.checkNotNullExpressionValue(generateKey, "generateKey(...)");
        return generateKey;
    }

    private final SecretKey getSecretKey() {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        if (keyStore.containsAlias(KEY_NAME)) {
            Key key = keyStore.getKey(KEY_NAME, null);
            Intrinsics.checkNotNull(key, "null cannot be cast to non-null type javax.crypto.SecretKey");
            return (SecretKey) key;
        }
        return generateSecretKey();
    }

    private final Cipher getCipher() {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        Intrinsics.checkNotNullExpressionValue(cipher, "getInstance(...)");
        return cipher;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void updateUI() {
        Function1<? super String, Unit> function1 = this.resultCallback;
        if (function1 != null) {
            String sb = this.results.toString();
            Intrinsics.checkNotNullExpressionValue(sb, "toString(...)");
            function1.invoke(sb);
        }
    }
}
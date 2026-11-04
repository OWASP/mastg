package org.owasp.mastestapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Base64;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.io.CloseableKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: MastgTest.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\n\u001a\u00020\u0007J\b\u0010\u000b\u001a\u00020\u0007H\u0002J\b\u0010\f\u001a\u00020\rH\u0002J\b\u0010\u000e\u001a\u00020\u000fH\u0002J\b\u0010\u0010\u001a\u00020\u000fH\u0002J\b\u0010\u0011\u001a\u00020\u000fH\u0002J\b\u0010\u0012\u001a\u00020\u000fH\u0002J\b\u0010\u0013\u001a\u00020\u000fH\u0002J\u0012\u0010\u0014\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0015\u001a\u00020\u0007H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0007X\u0082D¢\u0006\u0002\n\u0000Ê\u0001\f\b\u0017\u0012\b\b\u0018\u0012\u0004\b\u0003\u0010\u0000¨\u0006\u0016"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "tag", "", "sensitiveApiKey", "keyAlias", "mastgTest", "performSensitiveOperation", "getOrCreateSecretKey", "Ljavax/crypto/SecretKey;", "checkForSuBinary", "", "checkForWhichSu", "checkForRootPackages", "checkForTestKeys", "checkForDangerousProps", "getSystemProperty", "key", "app", "Landroidx/compose/runtime/internal/StabilityInferred;", "parameters"}, k = 1, mv = {2, 4, 0}, xi = 48)
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;
    private final String keyAlias;
    private final String sensitiveApiKey;
    private final String tag;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.tag = "MASTG";
        this.sensitiveApiKey = "sk-OWASP-MAS-SuperSecretKey-1234567890";
        this.keyAlias = "mastgCipherKey";
    }

    public final String mastgTest() {
        String operation;
        List checks = new ArrayList();
        Log.i(this.tag, "Starting root detection checks");
        boolean su = checkForSuBinary();
        checks.add(su ? "✓ Found su binary" : "✗ No su binary found");
        boolean whichSu = checkForWhichSu();
        checks.add(whichSu ? "✓ Found su via which command" : "✗ su not found via which command");
        boolean pkgs = checkForRootPackages();
        checks.add(pkgs ? "✓ Found root management apps" : "✗ No root management apps found");
        boolean testKeys = checkForTestKeys();
        checks.add(testKeys ? "✓ Device has test-keys build" : "✗ Device has release-keys build");
        boolean props = checkForDangerousProps();
        checks.add(props ? "✓ Found dangerous system properties" : "✗ No dangerous system properties");
        boolean isRooted = su || whichSu || pkgs || testKeys || props;
        Log.i(this.tag, "Completed checks: rooted=" + isRooted);
        if (isRooted) {
            Log.i(this.tag, "Sensitive operation skipped: root detected");
            operation = "The sensitive operation was not performed because the device seems to be rooted.";
        } else {
            operation = performSensitiveOperation();
        }
        return "Root Detection Results:\n\n" + CollectionsKt.joinToString$default(checks, "\n", null, null, 0, null, null, 62, null) + "\n\nDevice appears to be rooted: " + isRooted + "\n\n" + operation;
    }

    private final String performSensitiveOperation() {
        try {
            SecretKey key = getOrCreateSecretKey();
            Cipher encryptCipher = Cipher.getInstance("AES/GCM/NoPadding");
            encryptCipher.init(1, key);
            byte[] iv = encryptCipher.getIV();
            byte[] bytes = this.sensitiveApiKey.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            byte[] encryptedBytes = encryptCipher.doFinal(bytes);
            Intrinsics.checkNotNull(iv);
            Intrinsics.checkNotNull(encryptedBytes);
            String encryptedData = Base64.encodeToString(ArraysKt.plus(iv, encryptedBytes), 0);
            byte[] decodedData = Base64.decode(encryptedData, 0);
            Intrinsics.checkNotNull(decodedData);
            byte[] ivFromData = ArraysKt.copyOfRange(decodedData, 0, 12);
            byte[] ciphertext = ArraysKt.copyOfRange(decodedData, 12, decodedData.length);
            Cipher decryptCipher = Cipher.getInstance("AES/GCM/NoPadding");
            decryptCipher.init(2, key, new GCMParameterSpec(128, ivFromData));
            byte[] decryptedBytes = decryptCipher.doFinal(ciphertext);
            Intrinsics.checkNotNull(decryptedBytes);
            String decryptedString = new String(decryptedBytes, Charsets.UTF_8);
            boolean decryptionSuccessful = Intrinsics.areEqual(decryptedString, this.sensitiveApiKey);
            Log.i(this.tag, "Sensitive operation performed: decryption successful=" + decryptionSuccessful);
            return "Sensitive operation performed.\nEncrypted: " + encryptedData + "\nDecryption successful: " + decryptionSuccessful;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private final SecretKey getOrCreateSecretKey() throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException, NoSuchProviderException, UnrecoverableEntryException, InvalidAlgorithmParameterException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        if (keyStore.containsAlias(this.keyAlias)) {
            KeyStore.Entry entry = keyStore.getEntry(this.keyAlias, null);
            Intrinsics.checkNotNull(entry, "null cannot be cast to non-null type java.security.KeyStore.SecretKeyEntry");
            SecretKey secretKey = ((KeyStore.SecretKeyEntry) entry).getSecretKey();
            Intrinsics.checkNotNull(secretKey);
            return secretKey;
        }
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
        keyGenerator.init(new KeyGenParameterSpec.Builder(this.keyAlias, 3).setBlockModes("GCM").setEncryptionPaddings("NoPadding").build());
        SecretKey secretKeyGenerateKey = keyGenerator.generateKey();
        Intrinsics.checkNotNull(secretKeyGenerateKey);
        return secretKeyGenerateKey;
    }

    private final boolean checkForSuBinary() {
        String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        Log.d(this.tag, "checkForSuBinary: testing " + paths.length + " paths");
        boolean found = false;
        for (String path : paths) {
            try {
                boolean exists = new File(path).exists();
                Log.d(this.tag, "su path check: path=" + path + ", exists=" + exists);
                if (exists) {
                    found = true;
                }
            } catch (SecurityException se) {
                Log.w(this.tag, "su path check blocked: path=" + path + ", msg=" + se.getMessage());
            } catch (Throwable t) {
                Log.w(this.tag, "su path check error: path=" + path + ", err=" + t.getClass().getSimpleName() + ", msg=" + t.getMessage());
            }
        }
        Log.i(this.tag, "checkForSuBinary result: found=" + found);
        return found;
    }

    private final boolean checkForWhichSu() {
        int exit;
        try {
            Log.d(this.tag, "checkForWhichSu: executing which su");
            Process process = Runtime.getRuntime().exec(new String[]{"which", "su"});
            InputStream inputStream = process.getInputStream();
            Intrinsics.checkNotNullExpressionValue(inputStream, "getInputStream(...)");
            Reader inputStreamReader = new InputStreamReader(inputStream, Charsets.UTF_8);
            BufferedReader bufferedReader = inputStreamReader instanceof BufferedReader ? (BufferedReader) inputStreamReader : new BufferedReader(inputStreamReader, 8192);
            try {
                String stdout = StringsKt.trim((CharSequence) TextStreamsKt.readText(bufferedReader)).toString();
                CloseableKt.closeFinally(bufferedReader, null);
                InputStream errorStream = process.getErrorStream();
                Intrinsics.checkNotNullExpressionValue(errorStream, "getErrorStream(...)");
                Reader inputStreamReader2 = new InputStreamReader(errorStream, Charsets.UTF_8);
                BufferedReader bufferedReader2 = inputStreamReader2 instanceof BufferedReader ? (BufferedReader) inputStreamReader2 : new BufferedReader(inputStreamReader2, 8192);
                try {
                    String stderr = StringsKt.trim((CharSequence) TextStreamsKt.readText(bufferedReader2)).toString();
                    CloseableKt.closeFinally(bufferedReader2, null);
                    try {
                        exit = process.waitFor();
                    } catch (Throwable th) {
                        exit = -1;
                    }
                    boolean found = (stdout.length() > 0) && exit == 0;
                    String str = this.tag;
                    if (found) {
                        Log.i(str, "su found via which: path=" + stdout);
                    } else {
                        Log.d(str, "which su not found: exit=" + exit + ", stderr=" + stderr);
                    }
                    return found;
                } catch (Throwable th2) {
                    try {
                        throw th2;
                    } catch (Throwable th3) {
                        CloseableKt.closeFinally(bufferedReader2, th2);
                        throw th3;
                    }
                }
            } catch (Throwable th4) {
                try {
                    throw th4;
                } catch (Throwable th5) {
                    CloseableKt.closeFinally(bufferedReader, th4);
                    throw th5;
                }
            }
        } catch (SecurityException se) {
            Log.w(this.tag, "checkForWhichSu blocked: msg=" + se.getMessage());
            return false;
        } catch (Throwable t) {
            Log.w(this.tag, "checkForWhichSu error: err=" + t.getClass().getSimpleName() + ", msg=" + t.getMessage());
            return false;
        }
    }

    private final boolean checkForRootPackages() {
        String[] packages = {"com.noshufou.android.su", "com.noshufou.android.su.elite", "eu.chainfire.supersu", "com.koushikdutta.superuser", "com.thirdparty.superuser", "com.yellowes.su", "com.topjohnwu.magisk", "com.kingroot.kinguser", "com.kingo.root", "com.smedialink.oneclickroot", "com.zhiqupk.root.global", "com.alephzain.framaroot"};
        Log.d(this.tag, "checkForRootPackages: testing " + packages.length + " package names");
        boolean foundAny = false;
        for (String packageName : packages) {
            try {
                this.context.getPackageManager().getPackageInfo(packageName, 0);
                Log.i(this.tag, "root package detected: package=" + packageName);
                foundAny = true;
            } catch (PackageManager.NameNotFoundException e) {
                Log.d(this.tag, "root package not present: package=" + packageName);
            } catch (SecurityException se) {
                Log.w(this.tag, "package check blocked: package=" + packageName + ", msg=" + se.getMessage());
            } catch (Throwable t) {
                Log.w(this.tag, "package check error: package=" + packageName + ", err=" + t.getClass().getSimpleName() + ", msg=" + t.getMessage());
            }
        }
        Log.i(this.tag, "checkForRootPackages result: found=" + foundAny);
        return foundAny;
    }

    private final boolean checkForTestKeys() {
        String buildTags = Build.TAGS;
        boolean isTestKeys = false;
        if (buildTags != null && StringsKt.contains$default((CharSequence) buildTags, (CharSequence) "test-keys", false, 2, (Object) null)) {
            isTestKeys = true;
        }
        Log.i(this.tag, "checkForTestKeys: buildTags=" + buildTags + ", isTestKeys=" + isTestKeys);
        return isTestKeys;
    }

    private final boolean checkForDangerousProps() {
        Map dangerousProps = MapsKt.mapOf(TuplesKt.to("ro.debuggable", "1"), TuplesKt.to("ro.secure", "0"));
        Log.d(this.tag, "checkForDangerousProps: testing " + dangerousProps.size() + " properties");
        boolean matchedAny = false;
        for (Map.Entry entry : dangerousProps.entrySet()) {
            String prop = (String) entry.getKey();
            String expected = (String) entry.getValue();
            String actual = getSystemProperty(prop);
            boolean matched = Intrinsics.areEqual(actual, expected);
            Log.i(this.tag, "system property check: prop=" + prop + ", actual=" + actual + ", expected=" + expected + ", matched=" + matched);
            if (matched) {
                matchedAny = true;
            }
        }
        Log.i(this.tag, "checkForDangerousProps result: matched=" + matchedAny);
        return matchedAny;
    }

    private final String getSystemProperty(String key) {
        int exit;
        try {
            Log.d(this.tag, "getSystemProperty exec: key=" + key);
            Process process = Runtime.getRuntime().exec(new String[]{"getprop", key});
            InputStream inputStream = process.getInputStream();
            Intrinsics.checkNotNullExpressionValue(inputStream, "getInputStream(...)");
            Reader inputStreamReader = new InputStreamReader(inputStream, Charsets.UTF_8);
            BufferedReader bufferedReader = inputStreamReader instanceof BufferedReader ? (BufferedReader) inputStreamReader : new BufferedReader(inputStreamReader, 8192);
            try {
                String stdout = StringsKt.trim((CharSequence) TextStreamsKt.readText(bufferedReader)).toString();
                CloseableKt.closeFinally(bufferedReader, null);
                InputStream errorStream = process.getErrorStream();
                Intrinsics.checkNotNullExpressionValue(errorStream, "getErrorStream(...)");
                Reader inputStreamReader2 = new InputStreamReader(errorStream, Charsets.UTF_8);
                BufferedReader bufferedReader2 = inputStreamReader2 instanceof BufferedReader ? (BufferedReader) inputStreamReader2 : new BufferedReader(inputStreamReader2, 8192);
                try {
                    String stderr = StringsKt.trim((CharSequence) TextStreamsKt.readText(bufferedReader2)).toString();
                    CloseableKt.closeFinally(bufferedReader2, null);
                    try {
                        exit = process.waitFor();
                    } catch (Throwable th) {
                        exit = -1;
                    }
                    boolean z = stderr.length() > 0;
                    String str = this.tag;
                    if (z) {
                        Log.w(str, "getprop stderr: key=" + key + ", exit=" + exit + ", stderr=" + stderr);
                    } else {
                        Log.d(str, "getprop ok: key=" + key + ", exit=" + exit);
                    }
                    String str2 = stdout;
                    if (str2.length() == 0) {
                        str2 = null;
                    }
                    return str2;
                } catch (Throwable th2) {
                    try {
                        throw th2;
                    } catch (Throwable th3) {
                        CloseableKt.closeFinally(bufferedReader2, th2);
                        throw th3;
                    }
                }
            } catch (Throwable th4) {
                try {
                    throw th4;
                } catch (Throwable th5) {
                    CloseableKt.closeFinally(bufferedReader, th4);
                    throw th5;
                }
            }
        } catch (SecurityException se) {
            Log.w(this.tag, "getSystemProperty blocked: key=" + key + ", msg=" + se.getMessage());
            return null;
        } catch (Throwable t) {
            Log.w(this.tag, "getSystemProperty error: key=" + key + ", err=" + t.getClass().getSimpleName() + ", msg=" + t.getMessage());
            return null;
        }
    }
}

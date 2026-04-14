package org.owasp.mastestapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\u0012\n\u0002\b\u0002\b\u0007\u0018\u0000 \u00152\u00020\u0001:\u0001\u0015B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J \u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\rH\u0002J \u0010\u000e\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\rH\u0002J\u0012\u0010\u0010\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0002J\u0010\u0010\u0012\u001a\u00020\u00072\u0006\u0010\u0013\u001a\u00020\u0014H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0016"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "saveData", "", "key", "value", "useHmac", "", "loadData", "defaultValue", "calculateHmac", "data", "bytesToHex", "bytes", "", "Companion", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String PREFS_NAME = "app_settings";
    private static final String SECRET_KEY = "this-is-a-very-secret-key-for-the-demo";
    private final Context context;
    public static final int $stable = 8;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
        SharedPreferences prefs = this.context.getSharedPreferences(PREFS_NAME, 0);
        if (!prefs.contains("setup_complete")) {
            saveData("user_role_insecure", "user", false);
            saveData("user_role_secure", "user", true);
            Intrinsics.checkNotNull(prefs);
            SharedPreferences.Editor editor$iv = prefs.edit();
            editor$iv.putBoolean("setup_complete", true);
            editor$iv.commit();
            return "INITIAL SETUP COMPLETE.\n\nThe role for both secure and insecure tests has been set to 'user'.\n\nACTION REQUIRED:\n1. Use a file explorer or ADB shell on a rooted device.\n2. Go to: /data/data/org.owasp.mastestapp/shared_prefs/\n3. Open the file: app_settings.xml\n4. Change BOTH <string>user</string> values to <string>admin</string>.\n5. Save the file and run this test again to see the results.   OR use this ADB one-liner:\n   adb shell \"su -c 'sed -i \\\"s/>user<\\/>admin<\\/g\\\" /data/data/org.owasp.mastestapp/shared_prefs/app_settings.xml'\"\n";
        }
        StringBuilder results = new StringBuilder();
        results.append("--- VERIFYING SCENARIO 1: 'kind: fail' (No HMAC Protection) ---\n");
        String insecureRole = loadData("user_role_insecure", "error", false);
        results.append("Loaded role from 'user_role_insecure': '" + insecureRole + "'\n");
        if (Intrinsics.areEqual(insecureRole, "admin")) {
            results.append(">> OUTCOME: VULNERABLE. The application accepted the tampered 'admin' role because there was no integrity check.\n");
        } else {
            results.append(">> OUTCOME: NOT EXPLOITED. The role is still '" + insecureRole + "'. Please ensure you changed it to 'admin' in the XML file.\n");
        }
        results.append("\n--- VERIFYING SCENARIO 2: 'kind: pass' (HMAC Protection Enabled) ---\n");
        String secureRole = loadData("user_role_secure", "tampering_detected", true);
        results.append("Loaded role from 'user_role_secure': '" + secureRole + "'\n");
        if (Intrinsics.areEqual(secureRole, "tampering_detected")) {
            results.append(">> OUTCOME: SECURE. The application detected tampering and rejected the role.\n");
        } else if (Intrinsics.areEqual(secureRole, "admin")) {
            results.append(">> OUTCOME: UNEXPECTED. The role is 'admin', HMAC check failed.\n");
        } else {
            results.append(">> OUTCOME: NOT TAMPERED. The role is still '" + secureRole + "', and its HMAC is valid.\n");
        }
        results.append("\n\nTest complete.");
        String string = results.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    private final void saveData(String key, String value, boolean useHmac) throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
        SharedPreferences prefs = this.context.getSharedPreferences(PREFS_NAME, 0);
        Intrinsics.checkNotNull(prefs);
        SharedPreferences.Editor editor$iv = prefs.edit();
        editor$iv.putString(key, value);
        if (!useHmac) {
            Log.d("MASTG-TEST", "Saved data WITHOUT HMAC.");
        } else {
            String hmac = calculateHmac(value);
            if (hmac != null) {
                editor$iv.putString(key + "_hmac", hmac);
                Log.d("MASTG-TEST", "Saved data with HMAC.");
            }
        }
        editor$iv.commit();
    }

    private final String loadData(String key, String defaultValue, boolean useHmac) throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
        SharedPreferences prefs = this.context.getSharedPreferences(PREFS_NAME, 0);
        String value = prefs.getString(key, null);
        if (value == null) {
            return defaultValue;
        }
        if (useHmac) {
            String storedHmac = prefs.getString(key + "_hmac", null);
            if (storedHmac == null) {
                Log.w("MASTG-TEST", "HMAC verification failed: No HMAC found for key '" + key + "'.");
                return defaultValue;
            }
            String calculatedHmac = calculateHmac(value);
            if (Intrinsics.areEqual(storedHmac, calculatedHmac)) {
                Log.d("MASTG-TEST", "HMAC verification SUCCESS. Value is: " + value);
                return value;
            }
            Log.e("MASTG-TEST", "HMAC verification FAILED! Data has been tampered with.");
            return defaultValue;
        }
        Log.d("MASTG-TEST", "Loaded data without HMAC check. Value is: " + value);
        return value;
    }

    private final String calculateHmac(String data) throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            byte[] bytes = SECRET_KEY.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, HMAC_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] bytes2 = data.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
            byte[] hmacBytes = mac.doFinal(bytes2);
            Intrinsics.checkNotNull(hmacBytes);
            return bytesToHex(hmacBytes);
        } catch (InvalidKeyException e) {
            Log.e("MASTG-TEST", "Invalid HMAC key", e);
            return null;
        } catch (NoSuchAlgorithmException e2) {
            Log.e("MASTG-TEST", "HMAC algorithm not found", e2);
            return null;
        }
    }

    private final String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte element$iv : bytes) {
            result.append("0123456789abcdef".charAt((element$iv >> 4) & 15));
            result.append("0123456789abcdef".charAt(element$iv & 15));
        }
        String string = result.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }
}
package org.owasp.mastestapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\n\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u0000 \u001a2\u00020\u0001:\u0001\u001aB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J\u0010\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u0007H\u0002J\u0010\u0010\n\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u0007H\u0002J\b\u0010\u000b\u001a\u00020\fH\u0002J\u0018\u0010\r\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u0007H\u0002J\u0018\u0010\u000f\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u0007H\u0002J\u0018\u0010\u0010\u001a\u00020\u00072\u0006\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0002J\u0018\u0010\u0012\u001a\u00020\u00072\u0006\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0002J\u0010\u0010\u0013\u001a\u00020\u00072\u0006\u0010\u0014\u001a\u00020\u0007H\u0002J\u0010\u0010\u0015\u001a\u00020\u00072\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J\b\u0010\u0018\u001a\u00020\u0019H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001b"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "describeInsecure", "value", "describeSecure", "initializeDemoData", "", "savePlain", "key", "saveProtected", "loadPlain", "defaultValue", "loadProtected", "calculateHmac", "data", "bytesToHex", "bytes", "", "prefs", "Landroid/content/SharedPreferences;", "Companion", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    private static final String DEFAULT_ROLE = "user";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String KEY_ROLE_INSECURE = "user_role_insecure";
    private static final String KEY_ROLE_SECURE = "user_role_secure";
    private static final String KEY_ROLE_SECURE_HMAC = "user_role_secure_hmac";
    private static final String KEY_SETUP_COMPLETE = "setup_complete";
    private static final String PREFS_NAME = "app_settings";
    private static final String SECRET_KEY = "this-is-a-very-secret-key-for-the-demo";
    private static final String TAG = "MASTG-TEST";
    private static final String TAMPERING_DETECTED = "tampering_detected";
    private final Context context;
    public static final int $stable = 8;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
        SharedPreferences prefs = prefs();
        if (!prefs.contains(KEY_SETUP_COMPLETE)) {
            initializeDemoData();
            return "SETUP DONE.\n\nStored values:\ninsecure = user\nsecure = user\nsecure_hmac = HMAC(user)\n\nRun the app again after editing app_settings.xml.";
        }
        String insecureRole = loadPlain(KEY_ROLE_INSECURE, "error");
        String secureRole = loadProtected(KEY_ROLE_SECURE, TAMPERING_DETECTED);
        return StringsKt.trimIndent("\nStored now:\ninsecure = " + prefs.getString(KEY_ROLE_INSECURE, null) + "\nsecure = " + prefs.getString(KEY_ROLE_SECURE, null) + "\nsecure_hmac = " + prefs.getString(KEY_ROLE_SECURE_HMAC, null) + "\n\nLoaded:\ninsecure = " + insecureRole + "\nsecure = " + secureRole + "\n\nResult:\n" + describeInsecure(insecureRole) + "\n" + describeSecure(secureRole) + "\n");
    }

    private final String describeInsecure(String value) {
        return Intrinsics.areEqual(value, "admin") ? "❌ Insecure check bypassed." : Intrinsics.areEqual(value, DEFAULT_ROLE) ? "✅ Insecure value unchanged." : "⚠️ Insecure value unexpected.";
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue
    java.lang.NullPointerException: Cannot invoke "java.util.List.iterator()" because the return value of "jadx.core.dex.visitors.regions.SwitchOverStringVisitor$SwitchData.getNewCases()" is null
    	at jadx.core.dex.visitors.regions.SwitchOverStringVisitor.restoreSwitchOverString(SwitchOverStringVisitor.java:109)
    	at jadx.core.dex.visitors.regions.SwitchOverStringVisitor.visitRegion(SwitchOverStringVisitor.java:66)
    	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:77)
    	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:82)
    	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterative(DepthRegionTraversal.java:31)
    	at jadx.core.dex.visitors.regions.SwitchOverStringVisitor.visit(SwitchOverStringVisitor.java:60)
     */
    /* JADX WARN: Removed duplicated region for block: B:17:0x002c A[ORIG_RETURN, RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private final java.lang.String describeSecure(java.lang.String r2) {
        /*
            r1 = this;
            int r0 = r2.hashCode()
            switch(r0) {
                case -432068: goto L20;
                case 3599307: goto L14;
                case 92668751: goto L8;
                default: goto L7;
            }
        L7:
            goto L2c
        L8:
            java.lang.String r0 = "admin"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L11
            goto L7
        L11:
            java.lang.String r0 = "⚠️ Secure check bypassed with forged HMAC."
            goto L2e
        L14:
            java.lang.String r0 = "user"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L1d
            goto L7
        L1d:
            java.lang.String r0 = "✅ Secure value unchanged."
            goto L2e
        L20:
            java.lang.String r0 = "tampering_detected"
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L29
            goto L7
        L29:
            java.lang.String r0 = "✅ Secure check detected tampering."
            goto L2e
        L2c:
            java.lang.String r0 = "⚠️ Secure value unexpected."
        L2e:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.owasp.mastestapp.MastgTest.describeSecure(java.lang.String):java.lang.String");
    }

    private final void initializeDemoData() throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
        savePlain(KEY_ROLE_INSECURE, DEFAULT_ROLE);
        saveProtected(KEY_ROLE_SECURE, DEFAULT_ROLE);
        SharedPreferences $this$edit$iv = prefs();
        SharedPreferences.Editor editor$iv = $this$edit$iv.edit();
        editor$iv.putBoolean(KEY_SETUP_COMPLETE, true);
        editor$iv.commit();
    }

    private final void savePlain(String key, String value) {
        SharedPreferences $this$edit$iv = prefs();
        SharedPreferences.Editor editor$iv = $this$edit$iv.edit();
        editor$iv.putString(key, value);
        editor$iv.commit();
        Log.d(TAG, "Saved plain value, key=" + key + ", value=" + value);
    }

    private final void saveProtected(String key, String value) throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
        String hmac = calculateHmac(value);
        SharedPreferences $this$edit$iv = prefs();
        SharedPreferences.Editor editor$iv = $this$edit$iv.edit();
        editor$iv.putString(key, value);
        editor$iv.putString(key + "_hmac", hmac);
        editor$iv.commit();
        Log.d(TAG, "Saved protected value, key=" + key + ", value=" + value + ", hmac=" + hmac);
    }

    private final String loadPlain(String key, String defaultValue) {
        String string = prefs().getString(key, defaultValue);
        return string == null ? defaultValue : string;
    }

    private final String loadProtected(String key, String defaultValue) throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
        String storedHmac;
        String value = prefs().getString(key, null);
        if (value == null || (storedHmac = prefs().getString(key + "_hmac", null)) == null) {
            return defaultValue;
        }
        String calculatedHmac = calculateHmac(value);
        return Intrinsics.areEqual(storedHmac, calculatedHmac) ? value : defaultValue;
    }

    private final String calculateHmac(String data) throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            byte[] bytes = SECRET_KEY.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            SecretKeySpec key = new SecretKeySpec(bytes, HMAC_ALGORITHM);
            mac.init(key);
            byte[] bytes2 = data.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
            byte[] bArrDoFinal = mac.doFinal(bytes2);
            Intrinsics.checkNotNullExpressionValue(bArrDoFinal, "doFinal(...)");
            return bytesToHex(bArrDoFinal);
        } catch (InvalidKeyException e) {
            Log.e(TAG, "Invalid HMAC key", e);
            return "";
        } catch (NoSuchAlgorithmException e2) {
            Log.e(TAG, "HMAC algorithm not found", e2);
            return "";
        }
    }

    private final String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte element$iv : bytes) {
            int i = element$iv & UByte.MAX_VALUE;
            result.append("0123456789abcdef".charAt(i >>> 4));
            result.append("0123456789abcdef".charAt(i & 15));
        }
        String string = result.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    private final SharedPreferences prefs() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(PREFS_NAME, 0);
        Intrinsics.checkNotNullExpressionValue(sharedPreferences, "getSharedPreferences(...)");
        return sharedPreferences;
    }
}

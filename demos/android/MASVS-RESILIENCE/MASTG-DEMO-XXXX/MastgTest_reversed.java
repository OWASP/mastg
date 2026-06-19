package org.owasp.mastestapp;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import kotlin.io.FilesKt;
import kotlin.text.StringsKt;

public final class MastgTest {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String KEYSTORE_PROVIDER = "AndroidKeyStore";
    private static final String KEY_ALIAS = "mastg_demo_integrity_key";
    private static final String FILE_ROLE = "user_role.dat";
    private static final String FILE_ROLE_HMAC = "user_role.hmac";
    private static final String FILE_MARKER = ".setup_done";
    private static final String DEFAULT_ROLE = "user";
    private static final String TAG = "MASTG-DEMO";

    private final Context context;

    public MastgTest(Context context) {
        this.context = context;
    }

    public final String mastgTest() {
        File dir = this.context.getFilesDir();
        if (!new File(dir, FILE_MARKER).exists()) {
            setup(dir);
            FilesKt.writeBytes(new File(dir, FILE_MARKER), new byte[0]);
            return setupMessage(dir);
        }
        return verify(dir);
    }

    private final void setup(File dir) {
        byte[] bytes = DEFAULT_ROLE.getBytes(StandardCharsets.UTF_8);
        FilesKt.writeText(new File(dir, FILE_ROLE), DEFAULT_ROLE, StandardCharsets.UTF_8);
        FilesKt.writeText(new File(dir, FILE_ROLE_HMAC), computeHmac(bytes), StandardCharsets.UTF_8);
        Log.d(TAG, "Setup complete: role=" + DEFAULT_ROLE);
    }

    private final String verify(File dir) {
        File roleFile = new File(dir, FILE_ROLE);
        File hmacFile = new File(dir, FILE_ROLE_HMAC);
        if (!roleFile.exists() || !hmacFile.exists()) return "Error: data files missing.";

        byte[] payload = FilesKt.readBytes(roleFile);
        String stored = StringsKt.trim(FilesKt.readText(hmacFile, StandardCharsets.UTF_8)).toString();
        String computed = computeHmac(payload);

        boolean valid = MessageDigest.isEqual(
                stored.getBytes(StandardCharsets.UTF_8),
                computed.getBytes(StandardCharsets.UTF_8));
        String role = valid ? StringsKt.trim(new String(payload, StandardCharsets.UTF_8)).toString()
                            : "tampering_detected";
        Log.d(TAG, "role=" + role + " hmacValid=" + valid);

        return "=== ON-DISK VALUE ===\n"
                + "user_role.dat : " + StringsKt.trim(FilesKt.readText(roleFile, StandardCharsets.UTF_8)) + "\n"
                + "user_role.hmac: " + stored + "\n\n"
                + "=== RESULT ===\n"
                + "Role loaded : " + role + "\n"
                + "HMAC check  : " + (valid ? "PASSED — AndroidKeyStore key, unforgeable"
                                            : "FAILED — tampering detected");
    }

    private final String computeHmac(byte[] data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(getOrCreateKey());
            byte[] raw = mac.doFinal(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : raw) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            Log.e(TAG, "HMAC error", e);
            return "";
        }
    }

    private final SecretKey getOrCreateKey() throws Exception {
        KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER);
        ks.load(null);
        SecretKey existing = (SecretKey) ks.getKey(KEY_ALIAS, null);
        if (existing != null) return existing;
        KeyGenerator kg = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_HMAC_SHA256,
                KEYSTORE_PROVIDER);
        kg.init(new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .build());
        return kg.generateKey();
    }

    private final String setupMessage(File dir) {
        return "=== SETUP COMPLETE ===\n\n"
                + "Path  : " + dir.getAbsolutePath() + "/\n"
                + "Files : " + FILE_ROLE + ", " + FILE_ROLE_HMAC + "\n"
                + "Role  : \"" + DEFAULT_ROLE + "\"\n\n"
                + "=== TAMPER RECIPE ===\n\n"
                + "Step 1:  adb shell am force-stop org.owasp.mastestapp\n"
                + "Step 2:  adb shell \"run-as org.owasp.mastestapp sh -c"
                + " 'echo -n admin > files/user_role.dat'\"\n"
                + "Step 3:  adb shell am start -n org.owasp.mastestapp/.MainActivity\n\n"
                + "Tap 'Start': tampering is detected — the Keystore key cannot be extracted to forge the HMAC.";
    }
}
